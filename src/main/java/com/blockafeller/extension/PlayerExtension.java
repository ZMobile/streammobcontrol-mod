package com.blockafeller.extension;

import net.minecraft.util.Identifier;

public interface PlayerExtension {
    boolean isInhabiting();
    void setInhabiting(boolean value);

    Identifier getInhabitedMobType();
    void setInhabitedMobType(Identifier mobType);
}
