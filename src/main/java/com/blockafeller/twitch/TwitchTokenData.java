package com.blockafeller.twitch;

public class TwitchTokenData {
    private final String accessToken;
    private final String refreshToken;
    private final long expirationTime;

    public TwitchTokenData(String accessToken, String refreshToken, long expirationTime) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
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
}
