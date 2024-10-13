package com.blockafeller.twitch.memory;

public class ViewerDonationData {
    private String twitchUserId;
    private int totalBitsDonated;
    private int totalBitsProcessedIntoMobTime;
    private boolean isSubscribed;
    private int subscriptionTier;  // 0: not subscribed, 1: Tier 1, 2: Tier 2, 3: Tier 3
    private int sessionBitsDonated;  // Bits donated during the current stream session
    private long lastDonationTimestamp;  // Unix timestamp for the last donation

    // Constructors, getters, setters, etc.

    public ViewerDonationData(String twitchUserId) {
        this.twitchUserId = twitchUserId;
        this.totalBitsDonated = 0;
        this.totalBitsProcessedIntoMobTime = 0;
        this.isSubscribed = false;
        this.subscriptionTier = 0;
        this.sessionBitsDonated = 0;
        this.lastDonationTimestamp = 0;
    }

    // Getters and Setters
    public String getTwitchUserId() {
        return twitchUserId;
    }

    public void setTwitchUserId(String twitchUserId) {
        this.twitchUserId = twitchUserId;
    }

    public int getTotalBitsDonated() {
        return totalBitsDonated;
    }

    public void addBitsDonated(int bits) {
        this.totalBitsDonated += bits;
        this.sessionBitsDonated += bits;
        this.lastDonationTimestamp = System.currentTimeMillis();
    }

    public int getTotalBitsProcessedIntoMobTime() {
        return totalBitsProcessedIntoMobTime;
    }

    public void processBitsIntoMobTime(int bits) {
        this.totalBitsProcessedIntoMobTime += bits;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean isSubscribed, int tier) {
        this.isSubscribed = isSubscribed;
        this.subscriptionTier = tier;
    }

    public int getSubscriptionTier() {
        return subscriptionTier;
    }

    public int getSessionBitsDonated() {
        return sessionBitsDonated;
    }

    public void resetSessionBits() {
        this.sessionBitsDonated = 0;
    }

    public long getLastDonationTimestamp() {
        return lastDonationTimestamp;
    }
}
