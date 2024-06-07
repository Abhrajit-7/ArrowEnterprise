package com.arrow.Arrow.util;

public enum GameResult {
    //${number}_won;
    NUMBER_WON("Your number won!"),
    NUMBER_LOST("Your number lost.");

    private String message;
    private GameResult(String message){
        this.message=message;
    }

    private String getMessage(){
        return message;
    }

}
