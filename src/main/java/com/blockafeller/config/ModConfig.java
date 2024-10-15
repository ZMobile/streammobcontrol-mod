package com.blockafeller.config;

public class ModConfig  {
    private boolean kickCycle;
    private int gracePeriodSeconds;
    private String twitchAppClientId;
    private String twitchAppClientSecret;
    private boolean mobTimeLimitEnabled;
    private int bitsPerMobTimeMinute;
    private int minimumSubTierForMorphing;
    private int minimumBitsForMobTime;
    private int defaultSpectatorTimeLimit;
    private int spectatorSecondsGrantedForAuthCapacityFailure;
    private boolean punishSpectatorsForIntentionallyFailingToCompleteAuth;

    public ModConfig() {
        this.kickCycle = false;
        this.gracePeriodSeconds = 120;
        this.twitchAppClientId = null;
        this.twitchAppClientSecret = null;
        this.mobTimeLimitEnabled = true;
        this.bitsPerMobTimeMinute = 20;
        this.minimumSubTierForMorphing = 0;
        this.minimumBitsForMobTime = 0;
        this.defaultSpectatorTimeLimit = 180;
        this.spectatorSecondsGrantedForAuthCapacityFailure = 60;
        this.punishSpectatorsForIntentionallyFailingToCompleteAuth = true;
    }

    public boolean isKickCycle() {
        return kickCycle;
    }

    public void setKickCycle(boolean kickCycle) {
        this.kickCycle = kickCycle;
    }

    public int getGracePeriodSeconds() {
        return gracePeriodSeconds;
    }

    public void setGracePeriodSeconds(int gracePeriodSeconds) {
        this.gracePeriodSeconds = gracePeriodSeconds;
    }

    public String getTwitchAppClientId() {
        return twitchAppClientId;
    }

    public void setTwitchAppClientId(String twitchAppClientId) {
        this.twitchAppClientId = twitchAppClientId;
    }

    public String getTwitchAppClientSecret() {
        return twitchAppClientSecret;
    }

    public void setTwitchAppClientSecret(String twitchAppClientSecret) {
        this.twitchAppClientSecret = twitchAppClientSecret;
    }

    public boolean isMobTimeLimitEnabled() {
        return mobTimeLimitEnabled;
    }

    public void setMobTimeLimitEnabled(boolean mobTimeLimitEnabled) {
        this.mobTimeLimitEnabled = mobTimeLimitEnabled;
    }

    public int getBitsPerMobTimeMinute() {
        return bitsPerMobTimeMinute;
    }

    public void setBitsPerMobTimeMinute(int bitsPerMobTimeMinute) {
        this.bitsPerMobTimeMinute = bitsPerMobTimeMinute;
    }

    public int getMinimumSubTierForMorphing() {
        return minimumSubTierForMorphing;
    }

    public void setMinimumSubTierForMorphing(int minimumSubTierForMorphing) {
        this.minimumSubTierForMorphing = minimumSubTierForMorphing;
    }

    public int getMinimumBitsForMobTime() {
        return minimumBitsForMobTime;
    }

    public void setMinimumBitsForMobTime(int minimumBitsForMobTime) {
        this.minimumBitsForMobTime = minimumBitsForMobTime;
    }

    public int getDefaultSpectatorTimeLimit() {
        return defaultSpectatorTimeLimit;
    }

    public void setDefaultSpectatorTimeLimit(int defaultSpectatorTimeLimit) {
        this.defaultSpectatorTimeLimit = defaultSpectatorTimeLimit;
    }

    public int getSpectatorSecondsGrantedForAuthCapacityFailure() {
        return spectatorSecondsGrantedForAuthCapacityFailure;
    }

    public void setSpectatorSecondsGrantedForAuthCapacityFailure(int spectatorSecondsGrantedForAuthCapacityFailure) {
        this.spectatorSecondsGrantedForAuthCapacityFailure = spectatorSecondsGrantedForAuthCapacityFailure;
    }

    public boolean isPunishSpectatorsForIntentionallyFailingToCompleteAuth() {
        return punishSpectatorsForIntentionallyFailingToCompleteAuth;
    }

    public void setPunishSpectatorsForIntentionallyFailingToCompleteAuth(boolean punishSpectatorsForIntentionallyFailingToCompleteAuth) {
        this.punishSpectatorsForIntentionallyFailingToCompleteAuth = punishSpectatorsForIntentionallyFailingToCompleteAuth;
    }
}
