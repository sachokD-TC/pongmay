package com.google.example.games.basegameutils.multiplayer;

import android.support.annotation.NonNull;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;

public class MessageManager implements OnRealTimeMessageReceivedListener {
    @Override
    public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
        byte[] message = realTimeMessage.getMessageData();
        String messageStr = new String(message);
    }
}
