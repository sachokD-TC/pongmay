package com.google.example.games.basegameutils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.tasks.Task;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.google.example.games.basegameutils.GameHelper.SignInFailureReason;
import com.waasche.games.pongmay.helpers.ActionResolver;

public abstract class BaseGameActivity extends FragmentActivity implements GameHelperListener, ActionResolver {
    public static final int CLIENT_ALL = 11;
    public static final int CLIENT_GAMES = 1;
    public static final int CLIENT_PLUS = 2;
    private static final String TAG = "BaseGameActivity";
    private static final int RC_SIGN_IN = 1;
    protected boolean mDebugLog = false;
    protected GameHelper mHelper;
    protected int mRequestedClients = 1;
    private RealTimeMultiplayerClient realTimeMultiplayerClient;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

    protected BaseGameActivity() {
        if(account == null) startSignIn();
        realTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, account);
    }

    protected BaseGameActivity(int requestedClients) {
        setRequestedClients(requestedClients);
    }

    protected void setRequestedClients(int requestedClients) {
        this.mRequestedClients = requestedClients;
    }

    public GameHelper getGameHelper() {
        if (this.mHelper == null) {
            this.mHelper = new GameHelper(this, this.mRequestedClients);
            this.mHelper.enableDebugLog(this.mDebugLog);
        }
        return this.mHelper;
    }

    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (this.mHelper == null) {
            getGameHelper();
        }
        this.mHelper.setup(this);
    }

    protected void onStart() {
        super.onStart();
        this.mHelper.onStart(this);
    }

    protected void onStop() {
        super.onStop();
        this.mHelper.onStop();
    }

    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (response == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            account = GoogleSignIn.getLastSignedInAccount(this);
        }
    }

    protected GoogleApiClient getApiClient() {
        return this.mHelper.getApiClient();
    }

    public boolean isSignedIn() {
        return this.mHelper.isSignedIn();
    }

    protected void startSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void beginUserInitiatedSignIn() {
        this.mHelper.beginUserInitiatedSignIn();
    }

    public void signOut() {
        this.mHelper.signOut();
    }

    protected void showAlert(String message) {
        this.mHelper.makeSimpleDialog(message).show();
    }

    protected void showAlert(String title, String message) {
        this.mHelper.makeSimpleDialog(title, message).show();
    }

    protected void enableDebugLog(boolean enabled) {
        this.mDebugLog = true;
        if (this.mHelper != null) {
            this.mHelper.enableDebugLog(enabled);
        }
    }

    @Deprecated
    protected void enableDebugLog(boolean enabled, String tag) {
        Log.w(TAG, "BaseGameActivity.enabledDebugLog(bool,String) is deprecated. Use enableDebugLog(boolean)");
        enableDebugLog(enabled);
    }

    protected String getInvitationId() {
        return this.mHelper.getInvitationId();
    }

    protected void reconnectClient() {
        this.mHelper.reconnectClient();
    }

    protected boolean hasSignInError() {
        return this.mHelper.hasSignInError();
    }

    protected SignInFailureReason getSignInError() {
        return this.mHelper.getSignInError();
    }
}
