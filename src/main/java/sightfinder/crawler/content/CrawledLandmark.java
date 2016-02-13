package sightfinder.crawler.content;

import sightfinder.model.Landmark;
import sightfinder.model.LandmarkType;


public abstract class CrawledLandmark {

    public abstract Landmark toLandmark(LandmarkType type);

    public abstract String getCategory();
}
