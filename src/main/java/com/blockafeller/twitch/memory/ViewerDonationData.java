package com.blockafeller.twitch.memory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ViewerDonationData {
    private String twitchUserId;
    private int totalBitsDonated;
    private int totalBitsProcessedIntoMobTime;
    private boolean isSubscribed;
    private LocalDateTime subscriptionExpirationTime;
    private int subscriptionTier;  // 0: not subscribed, 1: Tier 1, 2: Tier 2, 3: Tier 3
    private int sessionBitsDonated;  // Bits donated during the current stream session
    private LocalDateTime lastDonationTimestamp;  // Unix timestamp for the last donation

    // Constructors, getters, setters, etc.

    public ViewerDonationData(String twitchUserId) {
        this.twitchUserId = twitchUserId;
        this.totalBitsDonated = 0;
        this.totalBitsProcessedIntoMobTime = 0;
        this.isSubscribed = false;
        this.subscriptionTier = 0;
        this.sessionBitsDonated = 0;
        this.lastDonationTimestamp = null;
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

    public synchronized void addBitsDonated(int bits) {
        this.totalBitsDonated += bits;
        this.sessionBitsDonated += bits;
        this.lastDonationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
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

    public synchronized void addSubscription(int tier) {
        this.isSubscribed = true;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Update subscription tier based on the new tier
        if (tier >= this.subscriptionTier) {
            // Set to higher tier if upgrading or renewing at the same tier
            this.subscriptionTier = tier;
        }

        // Reset the expiration time based on the new tier
        if (this.subscriptionExpirationTime == null || this.subscriptionExpirationTime.isBefore(now)) {
            this.subscriptionExpirationTime = now.plusMonths(1);
        } else {
            // Extend the subscription if adding time
            this.subscriptionExpirationTime = this.subscriptionExpirationTime.plusMonths(1);
        }

        // Update last donation timestamp (if necessary for your logic)
        this.lastDonationTimestamp = now;
    }

    public synchronized void addSubscription(int tier, LocalDateTime customExpirationTime) {
        this.isSubscribed = true;

        // Update subscription tier based on the new tier
        if (tier >= this.subscriptionTier) {
            this.subscriptionTier = tier;
        }

        // Set custom expiration time
        if (customExpirationTime.isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
            this.subscriptionExpirationTime = customExpirationTime;
        }

        // Update last donation timestamp (if necessary for your logic)
        this.lastDonationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
    }

    public synchronized void setSubscribed(boolean subscribed) {
        this.isSubscribed = subscribed;
    }

    public synchronized void setSubscriptionTier(int tier) {
        this.subscriptionTier = tier;
    }

    public LocalDateTime getSubscriptionExpirationTime() {
        return subscriptionExpirationTime;
    }

    public void setSubscriptionExpirationTime(LocalDateTime subscriptionExpirationTime) {
        this.subscriptionExpirationTime = subscriptionExpirationTime;
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

    public LocalDateTime getLastDonationTimestamp() {
        return lastDonationTimestamp;
    }
}
