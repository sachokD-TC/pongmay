package com.google.example.games.basegameutils.multiplayer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.example.games.basegameutils.GameHelper;

public class RoomUpdateCallback extends com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback {

    private GameHelper gameHelper;

    public RoomUpdateCallback(GameHelper gameHelper) {
        this.gameHelper = gameHelper;
    }

    @Override
    public void onRoomCreated(int i, @Nullable Room room) {
        if (room != null) {
            gameHelper.setRoomId(room.getRoomId());
            gameHelper.setPlayerId(room.getParticipantIds().get(0));
        }
    }

    @Override
    public void onJoinedRoom(int i, @Nullable Room room) {
        String string = "" + i;
    }

    @Override
    public void onLeftRoom(int i, @NonNull String s) {

    }

    @Override
    public void onRoomConnected(int i, @Nullable Room room) {
        String string = "connected" + i;
    }

}
