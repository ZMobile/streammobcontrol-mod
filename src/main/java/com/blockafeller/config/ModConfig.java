package com.blockafeller.config;

public class ModConfig  {
    private boolean kickCycle;
    private int gracePeriodSeconds;
    private String twitchAppClientId;

    public ModConfig() {
        this.kickCycle = true;
        this.gracePeriodSeconds = 120;
        this.twitchAppClientId = null;
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
}
