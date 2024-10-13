package com.blockafeller.twitch.memory;

public class PlayerAuthData {
    private String twitchUserId;
    private String twitchLogin;
    private String twitchDisplayName;

    public PlayerAuthData(String twitchUserId, String twitchLogin, String twitchDisplayName) {
        this.twitchUserId = twitchUserId;
        this.twitchLogin = twitchLogin;
        this.twitchDisplayName = twitchDisplayName;
    }
}
