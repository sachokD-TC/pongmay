package com.com.waasche.games.pongmay.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.*;
import com.google.android.gms.games.multiplayer.realtime.*;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig.Builder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.google.example.games.basegameutils.multiplayer.MultiplayerClient;
import com.waasche.games.pongmay.Pong;
import com.waasche.games.pongmay.R;
import com.waasche.games.pongmay.helpers.ActionResolver;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AndroidLauncher extends AndroidApplication implements ActionResolver, RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener {
    static final int RC_WAITING_ROOM = 10002;
    private static final int requestCode = 1;
    private static final int RC_SIGN_IN = 9001;
    private String creatorID;
    private GameHelper gameHelper;
    private String mRoomId;
    private String myID;
    private ArrayList<String> parcitipants;
    private Pong pong;
    private MultiplayerClient multiplayerClient;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private static final int RC_SELECT_PLAYERS = 9006;
    private RealTimeMultiplayerClient mRealTimeMultiplayerClient;

    /* renamed from: AndroidLauncher$2 */
    class C02052 implements Runnable {
        C02052() {
        }

        public void run() {
            Toast.makeText(AndroidLauncher.this.getApplicationContext(), "After signing in click again to show the leaderboard.", 0).show();
        }
    }

    /* renamed from: AndroidLauncher$3 */
    class C02063 implements Runnable {
        C02063() {
        }

        public void run() {
            AndroidLauncher.this.gameHelper.signOut();
        }
    }

    /* renamed from: AndroidLauncher$4 */
    class C02074 implements Runnable {
        C02074() {
        }

        public void run() {
            AndroidLauncher.this.getWindow().addFlags(128);
        }
    }

    /* renamed from: AndroidLauncher$1 */
    class C04061 implements GameHelperListener {
        C04061() {
        }

        public void onSignInFailed() {
        }

        public void onSignInSucceeded() {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContext();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignIn = GoogleSignIn.getClient(this, gso);
        googleSignIn.signOut();
        Intent signInIntent = googleSignIn.getSignInIntent();
        startActivityForResult(
                signInIntent, RC_SIGN_IN
        );
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        this.gameHelper = new GameHelper(this, 1);
        this.gameHelper.enableDebugLog(true);
        this.gameHelper.setGameContext(getContext());
        this.gameHelper.setup(new C04061());
        multiplayerClient = new MultiplayerClient(gameHelper, mRealTimeMultiplayerClient);
        this.pong = new Pong(this, multiplayerClient);
        initialize(this.pong, config);
    }

    private void startSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.app_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignIn = GoogleSignIn.getClient(this, gso);
        googleSignIn.signOut();
        Intent signInIntent = googleSignIn.getSignInIntent();
        startActivityForResult(
                signInIntent, RC_SIGN_IN
        );
    }

    protected void onStop() {
        this.gameHelper.onStop();
        super.onStop();
    }

    public void onActivityResult(int request, int response, Intent intent) {
        if (request == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                account = task.getResult(ApiException.class);
                multiplayerClient.setAccount(account);
                setupMultiplayer();
            } catch (ApiException e) {
                 System.out.print(e.getStatusCode());
            }
        }
        if (request != 10002) {
            super.onActivityResult(1, response, intent);
            this.gameHelper.onActivityResult(1, response, intent);
        } else if (response == -1) {
            int side;
            if (this.creatorID.equals(this.myID)) {
                side = 0;
                Toast.makeText(this, "YO EMPIEZO", 0).show();
            } else {
                side = 1;
                Toast.makeText(this, "TU EMPIEZAS", 0).show();
            }
        } else if (response == 0) {
            Games.RealTimeMultiplayer.leave(this.gameHelper.getApiClient(), this, this.mRoomId);
            getWindow().clearFlags(128);
        } else if (response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
            Games.RealTimeMultiplayer.leave(this.gameHelper.getApiClient(), this, this.mRoomId);
            getWindow().clearFlags(128);
        }
    }


    private void setupMultiplayer() {
        // update the clients
        mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, account);
        gameHelper.setMultiplayer(mRealTimeMultiplayerClient);
        // get the playerId from the PlayersClient
        PlayersClient playersClient = Games.getPlayersClient(this, account);
        playersClient.getCurrentPlayer().addOnSuccessListener(
                new OnSuccessListener<Player>() {
                    @Override
                    public void onSuccess(Player player) {
                        gameHelper.setPlayerId(player.getPlayerId());
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                gameHelper.setPlayerId("-1");
            }
        });
        multiplayerClient.setRealTimeMultiplayerClient(mRealTimeMultiplayerClient);
    }

    public void signIn() {
        try {
            runOnUiThread(new C02052());
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
        }
    }

    public void automaticSignIn() {
        this.gameHelper.beginUserInitiatedSignIn();
        this.gameHelper.getApiClient().connect();
    }

    public void signOut() {
        try {
            runOnUiThread(new C02063());
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
        }
    }

    public void rateGame() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("Your PlayStore Link")));
    }

    public void submitScoreWallMode(int highScore) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(this.gameHelper.getApiClient(), getString(C0204R.string.wall_mode_ranking_id), (long) highScore);
        }
    }

    public void displayLeaderboardWallMode() {
        if (isSignedIn()) {
            //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(this.gameHelper.getApiClient(), getString(C0204R.string.wall_mode_ranking_id)), 0);
        } else {
            automaticSignIn();
        }
    }

    public boolean isSignedIn() {
        return this.gameHelper.isSignedIn();
    }

    public void startQuickGame() {
        if (isSignedIn()) {
            Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);
            Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.setAutoMatchCriteria(am);
            RoomConfig roomConfig = roomConfigBuilder.build();
            runOnUiThread(new C02074());
            Log.d("BROZA", this.gameHelper.getApiClient().toString());
            Games.RealTimeMultiplayer.create(this.gameHelper.getApiClient(), roomConfig);
            return;
        }
        automaticSignIn();
    }

    private Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this).setMessageReceivedListener(this).setRoomStatusUpdateListener(this);
    }

    public void onJoinedRoom(int statusCode, Room room) {
        if (statusCode != 0) {
            Toast.makeText(getApplicationContext(), "Error joining room. Error Code : ", 0).show();
            return;
        }
        this.mRoomId = room.getRoomId();
        //startActivityForResult(Games.RealTimeMultiplayer.getWaitingRoomIntent(this.gameHelper.getApiClient(), room, 2), 10002);
    }

    public void onRoomCreated(int statusCode, Room room) {
        if (statusCode != 0) {
            Toast.makeText(getApplicationContext(), "Error creating room. Error Code : " + String.valueOf(statusCode), 0).show();
            return;
        }
        this.mRoomId = room.getRoomId();
        //startActivityForResult(Games.RealTimeMultiplayer.getWaitingRoomIntent(this.gameHelper.getApiClient(), room, 2), 10002);
    }

    public void onLeftRoom(int i, String s) {
    }

    public void onRoomConnected(int i, Room room) {
        this.mRoomId = room.getRoomId();
        this.parcitipants = room.getParticipantIds();
        this.creatorID = (String) this.parcitipants.get(0);
        this.myID = room.getParticipantId(Games.Players.getCurrentPlayerId(this.gameHelper.getApiClient()));
    }

    public void sendPos(float y, float restart) {
        try {
            Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(this.gameHelper.getApiClient(), ByteBuffer.allocate(8).putFloat(y).putFloat(restart).array(), this.mRoomId);
        } catch (Exception e) {
        }
    }

    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        ByteBuffer bf = ByteBuffer.wrap(rtm.getMessageData());
        float y = bf.getFloat();
        float restart = bf.getFloat();
        if (this.pong.getOnlineGame() != null) {
            this.pong.getOnlineGame().updateGame(y, restart);
        }
    }

    public void onRoomConnecting(Room room) {
    }

    public void onRoomAutoMatching(Room room) {
    }

    public void onPeerInvitedToRoom(Room room, List<String> list) {
    }

    public void onPeerDeclined(Room room, List<String> list) {
    }

    public void onPeerJoined(Room room, List<String> list) {
    }

    public void onPeerLeft(Room room, List<String> list) {
    }

    public void onConnectedToRoom(Room room) {
    }

    public void onDisconnectedFromRoom(Room room) {
        this.pong.getOnlineGame().updateGame(0.0f, 9.0f);
        Games.RealTimeMultiplayer.leave(this.gameHelper.getApiClient(), this, this.mRoomId);
        Toast.makeText(getApplicationContext(), "Error. Connection finished.", 0).show();
    }

    public void onPeersConnected(Room room, List<String> list) {
    }

    public void onPeersDisconnected(Room room, List<String> list) {
    }

    public void onP2PConnected(String s) {
    }

    public void onP2PDisconnected(String s) {
        Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(this.gameHelper.getApiClient(), ByteBuffer.allocate(8).putFloat(0.0f).putFloat(9.0f).array(), this.mRoomId);
    }
}
