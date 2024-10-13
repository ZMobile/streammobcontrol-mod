package com.blockafeller.twitch.memory;

import java.util.HashMap;
import java.util.Map;

public class ViewerDonationDataMap {
    private final Map<String, ViewerDonationData> twitchIdToViewerDonationDataMap = new HashMap<>();

    public ViewerDonationData getViewerDonationData(String twitchId) {
        return twitchIdToViewerDonationDataMap.get(twitchId);
    }

    public void putViewerDonationData(String twitchId, ViewerDonationData viewerDonationData) {
        twitchIdToViewerDonationDataMap.put(twitchId, viewerDonationData);
    }

    public void removeViewerDonationData(String twitchId) {
        twitchIdToViewerDonationDataMap.remove(twitchId);
    }
}
