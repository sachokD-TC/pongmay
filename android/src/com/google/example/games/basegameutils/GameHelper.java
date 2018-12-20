package com.google.example.games.basegameutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Games.GamesOptions;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.request.GameRequest;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import java.util.ArrayList;

public class GameHelper implements ConnectionCallbacks, OnConnectionFailedListener {
    public static final int CLIENT_ALL = 11;
    public static final int CLIENT_GAMES = 1;
    public static final int CLIENT_NONE = 0;
    public static final int CLIENT_PLUS = 2;
    public static final int CLIENT_SNAPSHOT = 8;
    static final int DEFAULT_MAX_SIGN_IN_ATTEMPTS = 3;
    static final int RC_RESOLVE = 9001;
    static final int RC_UNUSED = 9002;
    static final String TAG = "GameHelper";
    private final String GAMEHELPER_SHARED_PREFS = "GAMEHELPER_SHARED_PREFS";
    private final String KEY_SIGN_IN_CANCELLATIONS = "KEY_SIGN_IN_CANCELLATIONS";
    Activity mActivity = null;
    Context mAppContext = null;
    boolean mConnectOnStart = true;
    private boolean mConnecting = false;
    ConnectionResult mConnectionResult = null;
    boolean mDebugLog = false;
    boolean mExpectingResolution = false;
    GamesOptions mGamesApiOptions = GamesOptions.builder().build();
    GoogleApiClient mGoogleApiClient = null;
    Builder mGoogleApiClientBuilder = null;
    Handler mHandler;
    Invitation mInvitation;
    GameHelperListener mListener = null;
    int mMaxAutoSignInAttempts = 3;
    PlusOptions mPlusApiOptions = null;
    int mRequestedClients = 0;
    ArrayList<GameRequest> mRequests;
    private boolean mSetupDone = false;
    boolean mShowErrorDialogs = true;
    boolean mSignInCancelled = false;
    SignInFailureReason mSignInFailureReason = null;
    TurnBasedMatch mTurnBasedMatch;
    boolean mUserInitiatedSignIn = false;

    /* renamed from: com.google.example.games.basegameutils.GameHelper$1 */
    class C03051 implements Runnable {
        C03051() {
        }

        public void run() {
            GameHelper.this.notifyListener(false);
        }
    }

    public interface GameHelperListener {
        void onSignInFailed();

        void onSignInSucceeded();
    }

    public static class SignInFailureReason {
        public static final int NO_ACTIVITY_RESULT_CODE = -100;
        int mActivityResultCode;
        int mServiceErrorCode;

        public int getServiceErrorCode() {
            return this.mServiceErrorCode;
        }

        public int getActivityResultCode() {
            return this.mActivityResultCode;
        }

        public SignInFailureReason(int serviceErrorCode, int activityResultCode) {
            this.mServiceErrorCode = 0;
            this.mActivityResultCode = -100;
            this.mServiceErrorCode = serviceErrorCode;
            this.mActivityResultCode = activityResultCode;
        }

        public SignInFailureReason(int serviceErrorCode) {
            this(serviceErrorCode, -100);
        }

        public String toString() {
            String str;
            StringBuilder append = new StringBuilder().append("SignInFailureReason(serviceErrorCode:").append(GameHelperUtils.errorCodeToString(this.mServiceErrorCode));
            if (this.mActivityResultCode == -100) {
                str = ")";
            } else {
                str = ",activityResultCode:" + GameHelperUtils.activityResponseCodeToString(this.mActivityResultCode) + ")";
            }
            return append.append(str).toString();
        }
    }

    public GameHelper(Activity activity, int clientsToUse) {
        this.mActivity = activity;
        this.mAppContext = activity.getApplicationContext();
        this.mRequestedClients = clientsToUse;
        this.mHandler = new Handler();
    }

    public void setMaxAutoSignInAttempts(int max) {
        this.mMaxAutoSignInAttempts = max;
    }

    void assertConfigured(String operation) {
        if (!this.mSetupDone) {
            String error = "GameHelper error: Operation attempted without setup: " + operation + ". The setup() method must be called before attempting any other operation.";
            logError(error);
            throw new IllegalStateException(error);
        }
    }

    private void doApiOptionsPreCheck() {
        if (this.mGoogleApiClientBuilder != null) {
            String error = "GameHelper: you cannot call set*ApiOptions after the client builder has been created. Call it before calling createApiClientBuilder() or setup().";
            logError(error);
            throw new IllegalStateException(error);
        }
    }

    public void setGamesApiOptions(GamesOptions options) {
        doApiOptionsPreCheck();
        this.mGamesApiOptions = options;
    }

    public void setPlusApiOptions(PlusOptions options) {
        doApiOptionsPreCheck();
        this.mPlusApiOptions = options;
    }

    public Builder createApiClientBuilder() {
        if (this.mSetupDone) {
            String error = "GameHelper: you called GameHelper.createApiClientBuilder() after calling setup. You can only get a client builder BEFORE performing setup.";
            logError(error);
            throw new IllegalStateException(error);
        }
        Builder builder = new Builder(this.mActivity, this, this);
        if ((this.mRequestedClients & 1) != 0) {
            builder.addApi(Games.API, this.mGamesApiOptions);
            builder.addScope(Games.SCOPE_GAMES);
        }
        if ((this.mRequestedClients & 2) != 0) {
            builder.addApi(Plus.API);
            builder.addScope(Plus.SCOPE_PLUS_LOGIN);
        }
        if ((this.mRequestedClients & 8) != 0) {
            builder.addScope(Drive.SCOPE_APPFOLDER);
            builder.addApi(Drive.API);
        }
        this.mGoogleApiClientBuilder = builder;
        return builder;
    }

    public void setup(GameHelperListener listener) {
        if (this.mSetupDone) {
            String error = "GameHelper: you cannot call GameHelper.setup() more than once!";
            logError(error);
            throw new IllegalStateException(error);
        }
        this.mListener = listener;
        debugLog("Setup: requested clients: " + this.mRequestedClients);
        if (this.mGoogleApiClientBuilder == null) {
            createApiClientBuilder();
        }
        this.mGoogleApiClient = this.mGoogleApiClientBuilder.build();
        this.mGoogleApiClientBuilder = null;
        this.mSetupDone = true;
    }

    public GoogleApiClient getApiClient() {
        if (this.mGoogleApiClient != null) {
            return this.mGoogleApiClient;
        }
        throw new IllegalStateException("No GoogleApiClient. Did you call setup()?");
    }

    public boolean isSignedIn() {
        return this.mGoogleApiClient != null && this.mGoogleApiClient.isConnected();
    }

    public boolean isConnecting() {
        return this.mConnecting;
    }

    public boolean hasSignInError() {
        return this.mSignInFailureReason != null;
    }

    public SignInFailureReason getSignInError() {
        return this.mSignInFailureReason;
    }

    public void setShowErrorDialogs(boolean show) {
        this.mShowErrorDialogs = show;
    }

    public void onStart(Activity act) {
        this.mActivity = act;
        this.mAppContext = act.getApplicationContext();
        debugLog("onStart");
        assertConfigured("onStart");
        if (!this.mConnectOnStart) {
            debugLog("Not attempting to connect because mConnectOnStart=false");
            debugLog("Instead, reporting a sign-in failure.");
            this.mHandler.postDelayed(new C03051(), 1000);
        } else if (this.mGoogleApiClient.isConnected()) {
            Log.w(TAG, "GameHelper: client was already connected on onStart()");
        } else {
            debugLog("Connecting client.");
            this.mConnecting = true;
            this.mGoogleApiClient.connect();
        }
    }

    public void onStop() {
        debugLog("onStop");
        assertConfigured("onStop");
        if (this.mGoogleApiClient.isConnected()) {
            debugLog("Disconnecting client due to onStop");
            this.mGoogleApiClient.disconnect();
        } else {
            debugLog("Client already disconnected when we got onStop.");
        }
        this.mConnecting = false;
        this.mExpectingResolution = false;
        this.mActivity = null;
    }

    public String getInvitationId() {
        if (!this.mGoogleApiClient.isConnected()) {
            Log.w(TAG, "Warning: getInvitationId() should only be called when signed in, that is, after getting onSignInSuceeded()");
        }
        return this.mInvitation == null ? null : this.mInvitation.getInvitationId();
    }

    public Invitation getInvitation() {
        if (!this.mGoogleApiClient.isConnected()) {
            Log.w(TAG, "Warning: getInvitation() should only be called when signed in, that is, after getting onSignInSuceeded()");
        }
        return this.mInvitation;
    }

    public boolean hasInvitation() {
        return this.mInvitation != null;
    }

    public boolean hasTurnBasedMatch() {
        return this.mTurnBasedMatch != null;
    }

    public boolean hasRequests() {
        return this.mRequests != null;
    }

    public void clearInvitation() {
        this.mInvitation = null;
    }

    public void clearTurnBasedMatch() {
        this.mTurnBasedMatch = null;
    }

    public void clearRequests() {
        this.mRequests = null;
    }

    public TurnBasedMatch getTurnBasedMatch() {
        if (!this.mGoogleApiClient.isConnected()) {
            Log.w(TAG, "Warning: getTurnBasedMatch() should only be called when signed in, that is, after getting onSignInSuceeded()");
        }
        return this.mTurnBasedMatch;
    }

    public ArrayList<GameRequest> getRequests() {
        if (!this.mGoogleApiClient.isConnected()) {
            Log.w(TAG, "Warning: getRequests() should only be called when signed in, that is, after getting onSignInSucceeded()");
        }
        return this.mRequests;
    }

    public void enableDebugLog(boolean enabled) {
        this.mDebugLog = enabled;
        if (enabled) {
            debugLog("Debug log enabled.");
        }
    }

    @Deprecated
    public void enableDebugLog(boolean enabled, String tag) {
        Log.w(TAG, "GameHelper.enableDebugLog(boolean,String) is deprecated. Use GameHelper.enableDebugLog(boolean)");
        enableDebugLog(enabled);
    }

    public void signOut() {
        if (this.mGoogleApiClient.isConnected()) {
            if ((this.mRequestedClients & 2) != 0) {
                debugLog("Clearing default account on PlusClient.");
                Plus.AccountApi.clearDefaultAccount(this.mGoogleApiClient);
            }
            if ((this.mRequestedClients & 1) != 0) {
                debugLog("Signing out from the Google API Client.");
                Games.signOut(this.mGoogleApiClient);
            }
            debugLog("Disconnecting client.");
            this.mConnectOnStart = false;
            this.mConnecting = false;
            this.mGoogleApiClient.disconnect();
            return;
        }
        debugLog("signOut: was already disconnected, ignoring.");
    }

    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        String str;
        StringBuilder append = new StringBuilder().append("onActivityResult: req=");
        if (requestCode == 9001) {
            str = "RC_RESOLVE";
        } else {
            str = String.valueOf(requestCode);
        }
        debugLog(append.append(str).append(", resp=").append(GameHelperUtils.activityResponseCodeToString(responseCode)).toString());
        if (requestCode != 9001) {
            debugLog("onActivityResult: request code not meant for us. Ignoring.");
            return;
        }
        this.mExpectingResolution = false;
        if (!this.mConnecting) {
            debugLog("onActivityResult: ignoring because we are not connecting.");
        } else if (responseCode == -1) {
            debugLog("onAR: Resolution was RESULT_OK, so connecting current client again.");
            connect();
        } else if (responseCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            debugLog("onAR: Resolution was RECONNECT_REQUIRED, so reconnecting.");
            connect();
        } else if (responseCode == 0) {
            debugLog("onAR: Got a cancellation result, so disconnecting.");
            this.mSignInCancelled = true;
            this.mConnectOnStart = false;
            this.mUserInitiatedSignIn = false;
            this.mSignInFailureReason = null;
            this.mConnecting = false;
            this.mGoogleApiClient.disconnect();
            int prevCancellations = getSignInCancellations();
            debugLog("onAR: # of cancellations " + prevCancellations + " --> " + incrementSignInCancellations() + ", max " + this.mMaxAutoSignInAttempts);
            notifyListener(false);
        } else {
            debugLog("onAR: responseCode=" + GameHelperUtils.activityResponseCodeToString(responseCode) + ", so giving up.");
            giveUp(new SignInFailureReason(this.mConnectionResult.getErrorCode(), responseCode));
        }
    }

    void notifyListener(boolean success) {
        StringBuilder append = new StringBuilder().append("Notifying LISTENER of sign-in ");
        String str = success ? "SUCCESS" : this.mSignInFailureReason != null ? "FAILURE (error)" : "FAILURE (no error)";
        debugLog(append.append(str).toString());
        if (this.mListener == null) {
            return;
        }
        if (success) {
            this.mListener.onSignInSucceeded();
        } else {
            this.mListener.onSignInFailed();
        }
    }

    public void beginUserInitiatedSignIn() {
        debugLog("beginUserInitiatedSignIn: resetting attempt count.");
        resetSignInCancellations();
        this.mSignInCancelled = false;
        this.mConnectOnStart = true;
        if (this.mGoogleApiClient.isConnected()) {
            logWarn("beginUserInitiatedSignIn() called when already connected. Calling listener directly to notify of success.");
            notifyListener(true);
        } else if (this.mConnecting) {
            logWarn("beginUserInitiatedSignIn() called when already connecting. Be patient! You can only call this method after you get an onSignInSucceeded() or onSignInFailed() callback. Suggestion: disable the sign-in button on startup and also when it's clicked, and re-enable when you get the callback.");
        } else {
            debugLog("Starting USER-INITIATED sign-in flow.");
            this.mUserInitiatedSignIn = true;
            if (this.mConnectionResult != null) {
                debugLog("beginUserInitiatedSignIn: continuing pending sign-in flow.");
                this.mConnecting = true;
                resolveConnectionResult();
                return;
            }
            debugLog("beginUserInitiatedSignIn: starting new sign-in flow.");
            this.mConnecting = true;
            connect();
        }
    }

    void connect() {
        if (this.mGoogleApiClient.isConnected()) {
            debugLog("Already connected.");
            return;
        }
        debugLog("Starting connection.");
        this.mConnecting = true;
        this.mInvitation = null;
        this.mTurnBasedMatch = null;
        this.mGoogleApiClient.connect();
    }

    public void reconnectClient() {
        if (this.mGoogleApiClient.isConnected()) {
            debugLog("Reconnecting client.");
            this.mGoogleApiClient.reconnect();
            return;
        }
        Log.w(TAG, "reconnectClient() called when client is not connected.");
        connect();
    }

    public void onConnected(Bundle connectionHint) {
        debugLog("onConnected: connected!");
        if (connectionHint != null) {
            debugLog("onConnected: connection hint provided. Checking for invite.");
            Invitation inv = (Invitation) connectionHint.getParcelable(Multiplayer.EXTRA_INVITATION);
            if (!(inv == null || inv.getInvitationId() == null)) {
                debugLog("onConnected: connection hint has a room invite!");
                this.mInvitation = inv;
                debugLog("Invitation ID: " + this.mInvitation.getInvitationId());
            }
            this.mRequests = Games.Requests.getGameRequestsFromBundle(connectionHint);
            if (!this.mRequests.isEmpty()) {
                debugLog("onConnected: connection hint has " + this.mRequests.size() + " request(s)");
            }
            debugLog("onConnected: connection hint provided. Checking for TBMP game.");
            this.mTurnBasedMatch = (TurnBasedMatch) connectionHint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);
        }
        succeedSignIn();
    }

    void succeedSignIn() {
        debugLog("succeedSignIn");
        this.mSignInFailureReason = null;
        this.mConnectOnStart = true;
        this.mUserInitiatedSignIn = false;
        this.mConnecting = false;
        notifyListener(true);
    }

    int getSignInCancellations() {
        return this.mAppContext.getSharedPreferences("GAMEHELPER_SHARED_PREFS", 0).getInt("KEY_SIGN_IN_CANCELLATIONS", 0);
    }

    int incrementSignInCancellations() {
        int cancellations = getSignInCancellations();
        Editor editor = this.mAppContext.getSharedPreferences("GAMEHELPER_SHARED_PREFS", 0).edit();
        editor.putInt("KEY_SIGN_IN_CANCELLATIONS", cancellations + 1);
        editor.commit();
        return cancellations + 1;
    }

    void resetSignInCancellations() {
        Editor editor = this.mAppContext.getSharedPreferences("GAMEHELPER_SHARED_PREFS", 0).edit();
        editor.putInt("KEY_SIGN_IN_CANCELLATIONS", 0);
        editor.commit();
    }

    public void onConnectionFailed(ConnectionResult result) {
        boolean shouldResolve;
        debugLog("onConnectionFailed");
        this.mConnectionResult = result;
        debugLog("Connection failure:");
        debugLog("   - code: " + GameHelperUtils.errorCodeToString(this.mConnectionResult.getErrorCode()));
        debugLog("   - resolvable: " + this.mConnectionResult.hasResolution());
        debugLog("   - details: " + this.mConnectionResult.toString());
        int cancellations = getSignInCancellations();
        if (this.mUserInitiatedSignIn) {
            debugLog("onConnectionFailed: WILL resolve because user initiated sign-in.");
            shouldResolve = true;
        } else if (this.mSignInCancelled) {
            debugLog("onConnectionFailed WILL NOT resolve (user already cancelled once).");
            shouldResolve = false;
        } else if (cancellations < this.mMaxAutoSignInAttempts) {
            debugLog("onConnectionFailed: WILL resolve because we have below the max# of attempts, " + cancellations + " < " + this.mMaxAutoSignInAttempts);
            shouldResolve = true;
        } else {
            shouldResolve = false;
            debugLog("onConnectionFailed: Will NOT resolve; not user-initiated and max attempts reached: " + cancellations + " >= " + this.mMaxAutoSignInAttempts);
        }
        if (shouldResolve) {
            debugLog("onConnectionFailed: resolving problem...");
            resolveConnectionResult();
            return;
        }
        debugLog("onConnectionFailed: since we won't resolve, failing now.");
        this.mConnectionResult = result;
        this.mConnecting = false;
        notifyListener(false);
    }

    void resolveConnectionResult() {
        if (this.mExpectingResolution) {
            debugLog("We're already expecting the result of a previous resolution.");
        } else if (this.mActivity == null) {
            debugLog("No need to resolve issue, activity does not exist anymore");
        } else {
            debugLog("resolveConnectionResult: trying to resolve result: " + this.mConnectionResult);
            if (this.mConnectionResult.hasResolution()) {
                debugLog("Result has resolution. Starting it.");
                try {
                    this.mExpectingResolution = true;
                    this.mConnectionResult.startResolutionForResult(this.mActivity, 9001);
                    return;
                } catch (SendIntentException e) {
                    debugLog("SendIntentException, so connecting again.");
                    connect();
                    return;
                }
            }
            debugLog("resolveConnectionResult: result has no resolution. Giving up.");
            giveUp(new SignInFailureReason(this.mConnectionResult.getErrorCode()));
            this.mConnectionResult = null;
        }
    }

    public void disconnect() {
        if (this.mGoogleApiClient.isConnected()) {
            debugLog("Disconnecting client.");
            this.mGoogleApiClient.disconnect();
            return;
        }
        Log.w(TAG, "disconnect() called when client was already disconnected.");
    }

    void giveUp(SignInFailureReason reason) {
        this.mConnectOnStart = false;
        disconnect();
        this.mSignInFailureReason = reason;
        if (reason.mActivityResultCode == GamesActivityResultCodes.RESULT_APP_MISCONFIGURED) {
            GameHelperUtils.printMisconfiguredDebugInfo(this.mAppContext);
        }
        showFailureDialog();
        this.mConnecting = false;
        notifyListener(false);
    }

    public void onConnectionSuspended(int cause) {
        debugLog("onConnectionSuspended, cause=" + cause);
        disconnect();
        this.mSignInFailureReason = null;
        debugLog("Making extraordinary call to onSignInFailed callback");
        this.mConnecting = false;
        notifyListener(false);
    }

    public void showFailureDialog() {
        if (this.mSignInFailureReason != null) {
            int errorCode = this.mSignInFailureReason.getServiceErrorCode();
            int actResp = this.mSignInFailureReason.getActivityResultCode();
            if (this.mShowErrorDialogs) {
                showFailureDialog(this.mActivity, actResp, errorCode);
            } else {
                debugLog("Not showing error dialog because mShowErrorDialogs==false. Error was: " + this.mSignInFailureReason);
            }
        }
    }

    public static void showFailureDialog(Activity activity, int actResp, int errorCode) {
        if (activity == null) {
            Log.e(TAG, "*** No Activity. Can't show failure dialog!");
            return;
        }
        Dialog errorDialog;
        switch (actResp) {
            case GamesActivityResultCodes.RESULT_SIGN_IN_FAILED /*10002*/:
                errorDialog = makeSimpleDialog(activity, GameHelperUtils.getString(activity, 1));
                break;
            case GamesActivityResultCodes.RESULT_LICENSE_FAILED /*10003*/:
                errorDialog = makeSimpleDialog(activity, GameHelperUtils.getString(activity, 3));
                break;
            case GamesActivityResultCodes.RESULT_APP_MISCONFIGURED /*10004*/:
                errorDialog = makeSimpleDialog(activity, GameHelperUtils.getString(activity, 2));
                break;
            default:
                errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, activity, 9002, null);
                if (errorDialog == null) {
                    Log.e(TAG, "No standard error dialog available. Making fallback dialog.");
                    errorDialog = makeSimpleDialog(activity, GameHelperUtils.getString(activity, 0) + " " + GameHelperUtils.errorCodeToString(errorCode));
                    break;
                }
                break;
        }
        errorDialog.show();
    }

    static Dialog makeSimpleDialog(Activity activity, String text) {
        return new AlertDialog.Builder(activity).setMessage(text).setNeutralButton(17039370, null).create();
    }

    static Dialog makeSimpleDialog(Activity activity, String title, String text) {
        return new AlertDialog.Builder(activity).setMessage(text).setTitle(title).setNeutralButton(17039370, null).create();
    }

    public Dialog makeSimpleDialog(String text) {
        if (this.mActivity != null) {
            return makeSimpleDialog(this.mActivity, text);
        }
        logError("*** makeSimpleDialog failed: no current Activity!");
        return null;
    }

    public Dialog makeSimpleDialog(String title, String text) {
        if (this.mActivity != null) {
            return makeSimpleDialog(this.mActivity, title, text);
        }
        logError("*** makeSimpleDialog failed: no current Activity!");
        return null;
    }

    void debugLog(String message) {
        if (this.mDebugLog) {
            Log.d(TAG, "GameHelper: " + message);
        }
    }

    void logWarn(String message) {
        Log.w(TAG, "!!! GameHelper WARNING: " + message);
    }

    void logError(String message) {
        Log.e(TAG, "*** GameHelper ERROR: " + message);
    }

    public void setConnectOnStart(boolean connectOnStart) {
        debugLog("Forcing mConnectOnStart=" + connectOnStart);
        this.mConnectOnStart = connectOnStart;
    }
}
