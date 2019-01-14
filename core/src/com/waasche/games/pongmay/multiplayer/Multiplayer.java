package com.waasche.games.pongmay.multiplayer;

public interface Multiplayer {
    int[] getRacketCoordinates();
    void sendCoordinatesOfRacket(float[] coodrinates);
    void createRoom();
    void joinRoom();
}
