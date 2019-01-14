package com.google.example.games.basegameutils.multiplayer;

import com.google.android.gms.games.RealTimeMultiplayerClient;

public class RealTimeReliableMessageSentListener implements RealTimeMultiplayerClient.ReliableMessageSentCallback {
    @Override
    public void onRealTimeMessageSent(int i, int i1, String s) {
        //7001
        String message = "sended";
    }
}
