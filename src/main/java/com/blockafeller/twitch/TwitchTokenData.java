package com.blockafeller.twitch;

public class TwitchTokenData {
    private final String accessToken;
    private final String refreshToken;
    private final long expirationTime;
    private int responseCode;

    public TwitchTokenData(String accessToken, String refreshToken, long expirationTime, int responseCode) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
        this.responseCode = responseCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
