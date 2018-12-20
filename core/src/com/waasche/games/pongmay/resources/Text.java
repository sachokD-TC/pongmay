package com.waasche.games.pongmay.resources;

import com.waasche.games.pongmay.Pong;

public class Text {
    public static String get(String name){
        return Pong.getInstance().pongBundle.get(name);
    }
}
