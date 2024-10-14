package com.blockafeller.twitch;
public class TwitchAuthorizationInitializationData {
    private String deviceCode;
    private int expiresIn;
    private int interval;
    private String userCode;
    private String verificationUri;
    private int reponseCode;

    public TwitchAuthorizationInitializationData(String deviceCode, int expiresIn, int interval, String userCode, String verificationUri, int reponseCode) {
        this.deviceCode = deviceCode;
        this.expiresIn = expiresIn;
        this.interval = interval;
        this.userCode = userCode;
        this.verificationUri = verificationUri;
        this.reponseCode = reponseCode;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public int getInterval() {
        return interval;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getVerificationUri() {
        return verificationUri;
    }

    public int getReponseCode() {
        return reponseCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
}
