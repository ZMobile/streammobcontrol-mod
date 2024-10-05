package com.blockafeller.config;

public class ModConfig  {
    private boolean kickCycle;

    public ModConfig() {
        this.kickCycle = true;
    }

    public boolean isKickCycle() {
        return kickCycle;
    }

    public void setKickCycle(boolean kickCycle) {
        this.kickCycle = kickCycle;
    }
}
