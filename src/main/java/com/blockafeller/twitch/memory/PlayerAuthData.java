package com.blockafeller.twitch.memory;

public class PlayerAuthData {
    private String twitchUserId;
    private String twitchLogin;
    private String twitchDisplayName;
    private Integer responseCode;

    public PlayerAuthData(String twitchUserId, String twitchLogin, String twitchDisplayName, int responseCode) {
        this.twitchUserId = twitchUserId;
        this.twitchLogin = twitchLogin;
        this.twitchDisplayName = twitchDisplayName;
        this.responseCode = responseCode;
    }

    public PlayerAuthData(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getTwitchUserId() {
        return twitchUserId;
    }

    public void setTwitchUserId(String twitchUserId) {
        this.twitchUserId = twitchUserId;
    }

    public String getTwitchLogin() {
        return twitchLogin;
    }

    public void setTwitchLogin(String twitchLogin) {
        this.twitchLogin = twitchLogin;
    }

    public String getTwitchDisplayName() {
        return twitchDisplayName;
    }

    public void setTwitchDisplayName(String twitchDisplayName) {
        this.twitchDisplayName = twitchDisplayName;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }
}
