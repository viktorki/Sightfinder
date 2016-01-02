package sightfinder.crawler.content;

import java.util.ArrayList;
import java.util.List;

public final class LandmarkData {

    private static List<CrawledLandmark> landmarkList = new ArrayList<CrawledLandmark>();

    public static List<CrawledLandmark> getAndClearCollectedLandmarks() {
        return landmarkList;
    }

    public static synchronized void addLandmark(CrawledLandmark landmark) {
        landmarkList.add(landmark);
    }

}
