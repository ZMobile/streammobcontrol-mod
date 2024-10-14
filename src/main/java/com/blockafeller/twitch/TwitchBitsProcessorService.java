package com.blockafeller.twitch;

import com.blockafeller.config.ConfigManager;
import com.blockafeller.time.PlayerTimeData;
import com.blockafeller.time.PlayerTimeDataManager;
import com.blockafeller.twitch.memory.PlayerAuthData;
import com.blockafeller.twitch.memory.PlayerAuthDataManager;
import com.blockafeller.twitch.memory.ViewerDonationData;
import com.blockafeller.twitch.memory.ViewerDonationDataManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TwitchBitsProcessorService {
    public static void processBitsIntoMobTime(ServerPlayerEntity player) {
        if (!PlayerAuthDataManager.getPlayerAuthDataMap().hasAuthData(player.getUuid())) {
            return;
        }
        PlayerAuthData playerAuthData = PlayerAuthDataManager.getPlayerAuthDataMap().getAuthData(player.getUuid());
        int bitsPerMobTimeMinute = ConfigManager.getConfig().getBitsPerMobTimeMinute();
        if (bitsPerMobTimeMinute == 0) {
            return;
        }
        int minimumBitsForMobTime = ConfigManager.getConfig().getMinimumBitsForMobTime();
        ViewerDonationData viewerDonationData = ViewerDonationDataManager.getViewerDonationDataMap().getViewerDonationData(playerAuthData.getTwitchUserId());
        if (viewerDonationData == null) {
            throw new RuntimeException("Viewer donation data not found.");
        }

        int unprocessedBits = viewerDonationData.getTotalBitsDonated() - viewerDonationData.getTotalBitsProcessedIntoMobTime();
        if (unprocessedBits < minimumBitsForMobTime) {
            return;
        }

        int bitsBeingProcessed;
        if (minimumBitsForMobTime == 0) {
            bitsBeingProcessed = unprocessedBits;
        } else {
            bitsBeingProcessed = unprocessedBits - (unprocessedBits % minimumBitsForMobTime);
        }

        int mobTimeSecondsToAdd = (bitsBeingProcessed / bitsPerMobTimeMinute) * 60;

        PlayerTimeData data = PlayerTimeDataManager.getOrCreatePlayerTimeData(player.getUuid(), player.getServer());

        data.setMobTime(data.getMobTime() + mobTimeSecondsToAdd);
        data.setTotalMobTime(data.getTotalMobTime() + mobTimeSecondsToAdd);

        viewerDonationData.processBitsIntoMobTime(bitsBeingProcessed);

        ViewerDonationDataManager.putViewerDonationData(playerAuthData.getTwitchUserId(), viewerDonationData);

        player.sendMessage(Text.literal("Processed " + bitsBeingProcessed + " to add " + mobTimeSecondsToAdd + " seconds of mob time."));
    }
}
