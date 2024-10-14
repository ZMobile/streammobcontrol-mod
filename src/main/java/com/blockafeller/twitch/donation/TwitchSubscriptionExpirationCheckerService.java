package com.blockafeller.twitch.donation;

import com.blockafeller.twitch.memory.PlayerAuthData;
import com.blockafeller.twitch.memory.PlayerAuthDataManager;
import com.blockafeller.twitch.memory.ViewerDonationData;
import com.blockafeller.twitch.memory.ViewerDonationDataManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TwitchSubscriptionExpirationCheckerService {
    public static void checkSubscriptions(ServerPlayerEntity player) {
        PlayerAuthData playerAuthData = PlayerAuthDataManager.getPlayerAuthDataMap().getAuthData(player.getUuid());
        if (playerAuthData == null) {
            return;
        }

        ViewerDonationData viewerDonationData = ViewerDonationDataManager.getViewerDonationDataMap().getViewerDonationData(playerAuthData.getTwitchUserId());
        if (viewerDonationData == null) {
            viewerDonationData = new ViewerDonationData(playerAuthData.getTwitchUserId());
            ViewerDonationDataManager.getViewerDonationDataMap().putViewerDonationData(playerAuthData.getTwitchUserId(), viewerDonationData);
        }

        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);

        if (viewerDonationData.getSubscriptionExpirationTime() != null &&
                viewerDonationData.getSubscriptionExpirationTime().isBefore(currentTime)) {
            viewerDonationData.setSubscriptionTier(0);
            viewerDonationData.setSubscriptionExpirationTime(null);
        }

        ViewerDonationDataManager.saveViewerDonationData();
    }
}
