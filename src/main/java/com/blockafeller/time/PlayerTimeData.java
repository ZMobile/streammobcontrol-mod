package com.blockafeller.time;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class PlayerTimeData extends PersistentState {
    private long mobTime;       // Mob time in seconds
    private long spectatorTime; // Spectator time in seconds

    // Constructor to initialize default values
    public PlayerTimeData() {
        this.mobTime = 0;
        this.spectatorTime = 0;
    }

    // Load data from NBT (when the player data is loaded)
    public static PlayerTimeData fromNbt(NbtCompound nbt) {
        PlayerTimeData data = new PlayerTimeData();
        data.mobTime = nbt.getLong("MobTime");
        data.spectatorTime = nbt.getLong("SpectatorTime");
        return data;
    }

    // Write data to NBT (when the player data is saved)
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLong("MobTime", this.mobTime);
        nbt.putLong("SpectatorTime", this.spectatorTime);
        return nbt;
    }

    // Getters and setters
    public long getMobTime() {
        return mobTime;
    }

    public void setMobTime(long mobTime) {
        this.mobTime = mobTime;
        markDirty(); // Mark data as dirty so it gets saved
    }

    public long getSpectatorTime() {
        return spectatorTime;
    }

    public void setSpectatorTime(long spectatorTime) {
        this.spectatorTime = spectatorTime;
        markDirty(); // Mark data as dirty so it gets saved
    }

    public void decrementMobTime(long seconds) {
        this.mobTime = Math.max(0, this.mobTime - seconds);
        markDirty();
    }

    public void decrementSpectatorTime(long seconds) {
        this.spectatorTime = Math.max(0, this.spectatorTime - seconds);
        markDirty();
    }
}
