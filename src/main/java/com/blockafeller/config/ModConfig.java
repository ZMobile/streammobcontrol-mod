package com.blockafeller.config;

public class ModConfig  {
    private boolean kickCycle;
    private int gracePeriodSeconds;

    public ModConfig() {
        this.kickCycle = true;
        this.gracePeriodSeconds = 120;
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
}
