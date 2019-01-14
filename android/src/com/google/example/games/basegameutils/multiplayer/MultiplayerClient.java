package com.google.example.games.basegameutils.multiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.example.games.basegameutils.GameHelper;
import com.waasche.games.pongmay.multiplayer.Multiplayer;

import java.util.List;

public class MultiplayerClient extends FragmentActivity implements Multiplayer {

    private static final int RC_SIGN_IN = 9001;
    public static final int HOST_ID = 0;
    public static final String TENNIS_INVITE_ID = "tennis";
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private RealTimeMultiplayerClient realTimeMultiplayerClient;
    private GameHelper gameHelper;
    private RoomConfig mJoinedRoomConfig;
    private RoomUpdateCallback roomUpdateCallback;

    public MultiplayerClient(GameHelper gameHelper, RealTimeMultiplayerClient realTimeMultiplayerClient) {
        this.gameHelper = gameHelper;
        this.realTimeMultiplayerClient = realTimeMultiplayerClient;
        roomUpdateCallback = new com.google.example.games.basegameutils.multiplayer.RoomUpdateCallback(gameHelper);
    }

    public void setAccount(GoogleSignInAccount account) {
        this.account = account;
    }

    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    }

    public RealTimeMultiplayerClient getRealTimeMultiplayerClient() {
        return realTimeMultiplayerClient;
    }

    public void sendCoordinatesOfRacket(float[] coodrinates) {
        String message = "" + coodrinates[0] + "," + coodrinates[1];
        byte[] mMsgBuf = new byte[message.length()];
        for (int position = 0; position < message.length(); position++)
            mMsgBuf[position] = (byte) message.charAt(position);
        if (gameHelper.getRoomId() != null && gameHelper.getPlayerId() != null) {
            realTimeMultiplayerClient.sendReliableMessage(mMsgBuf, gameHelper.getRoomId(), gameHelper.getPlayerId(), new RealTimeReliableMessageSentListener()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String ee = e.getLocalizedMessage();
                }
            }).addOnSuccessListener(new OnSuccessListener<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                String ee = ""+ integer;
            }
            });
        }
    }

    public void setRealTimeMultiplayerClient(RealTimeMultiplayerClient realTimeMultiplayerClient) {
        this.realTimeMultiplayerClient = realTimeMultiplayerClient;
    }

    @Override
    public int[] getRacketCoordinates() {
        return new int[]{1, 1};
    }

    @Override
    public void createRoom() {
        RoomStatusUpdateCallback roomStatusUpdateCallback = new RoomStatusUpdateCallback() {
            @Override
            public void onRoomConnecting(@Nullable Room room) {

            }

            @Override
            public void onRoomAutoMatching(@Nullable Room room) {

            }

            @Override
            public void onPeerInvitedToRoom(@Nullable Room room, @NonNull List<String> list) {

            }

            @Override
            public void onPeerDeclined(@Nullable Room room, @NonNull List<String> list) {

            }

            @Override
            public void onPeerJoined(@Nullable Room room, @NonNull List<String> list) {

            }

            @Override
            public void onPeerLeft(@Nullable Room room, @NonNull List<String> list) {

            }

            @Override
            public void onConnectedToRoom(@Nullable Room room) {
                String string = "connected";
            }

            @Override
            public void onDisconnectedFromRoom(@Nullable Room room) {

            }

            @Override
            public void onPeersConnected(@Nullable Room room, @NonNull List<String> list) {

            }

            @Override
            public void onPeersDisconnected(@Nullable Room room, @NonNull List<String> list) {

            }

            @Override
            public void onP2PConnected(@NonNull String s) {

            }

            @Override
            public void onP2PDisconnected(@NonNull String s) {

            }
        };
        Bundle bundle = RoomConfig.createAutoMatchCriteria(1, 1, 0);;
        RoomConfig roomConfig = RoomConfig.builder(roomUpdateCallback)
                .setOnMessageReceivedListener(new MessageManager())
                .setRoomStatusUpdateCallback(roomStatusUpdateCallback)
                .setAutoMatchCriteria(bundle)
                .setInvitationIdToAccept(TENNIS_INVITE_ID)
                .build();
        mJoinedRoomConfig = roomConfig;
        realTimeMultiplayerClient.create(roomConfig);
    }

    public void joinRoom() {
        if(gameHelper.getRoomId() != null) {
            Games.getGamesClient(gameHelper.getGameContext(), account).getActivationHint().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String ee = e.getLocalizedMessage();
                }
            })
                    .addOnSuccessListener(
                            new OnSuccessListener<Bundle>() {
                                @Override
                                public void onSuccess(Bundle bundle) {
                                    if (bundle != null) {
                                        Invitation invitation = bundle.getParcelable("invitation");
                                        if (invitation != null) {
                                            RoomConfig.Builder builder = RoomConfig.builder(roomUpdateCallback)
                                                    .setInvitationIdToAccept(invitation.getInvitationId());
                                            mJoinedRoomConfig = builder.build();
                                            realTimeMultiplayerClient.join(mJoinedRoomConfig);
                                            // prevent screen from sleeping during handshake
                                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                        }
                                    }
                                }
                            }
                    );
        }
    }
}
