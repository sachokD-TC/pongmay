package com.waasche.games.pongmay.helpers;

public interface ActionResolver {
    void automaticSignIn();

    void displayLeaderboardWallMode();

    boolean isSignedIn();

    void rateGame();

    void sendPos(float f, float f2);

    void signIn();

    void signOut();

    void startQuickGame();

    void submitScoreWallMode(int i);
}
