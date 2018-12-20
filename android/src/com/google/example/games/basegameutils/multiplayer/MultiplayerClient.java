package com.google.example.games.basegameutils.multiplayer;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.waasche.games.pongmay.multiplayer.Multiplayer;

public class MultiplayerClient extends FragmentActivity implements Multiplayer {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private RealTimeMultiplayerClient realTimeMultiplayerClient;

    public MultiplayerClient(GoogleSignInClient googleSignInClient){
        this.mGoogleSignInClient = googleSignInClient;
    }

    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    }

    public RealTimeMultiplayerClient getRealTimeMultiplayerClient(){
        return realTimeMultiplayerClient;
    }

    @Override
    public int[] getRacketCoordinates() {
        return new int[]{1,1};
    }

    @Override
    public void connect() {
        //startSignIn();
    }
}
