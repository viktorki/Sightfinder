package sightfinder.crawler.content;

import sightfinder.model.Landmark;
import sightfinder.model.LandmarkType;
import sightfinder.util.Source;

public class BGTourismLandmark extends CrawledLandmark {

    private String name;

    private String category;

    private String description;

    @Override
    public String getCategory() {
        return this.category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Landmark toLandmark(LandmarkType landmarkType) {
        Landmark landmark = new Landmark();
        landmark.setName(name);
        landmark.setLandmarkType(landmarkType);
        landmark.setDescription(description);
        landmark.setSource(Source.BG_TOURISM);
        return landmark;
    }
}