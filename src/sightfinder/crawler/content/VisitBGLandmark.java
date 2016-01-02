package sightfinder.crawler.content;

import sightfinder.model.Landmark;
import sightfinder.model.LandmarkType;
import sightfinder.util.Source;

public class VisitBGLandmark extends CrawledLandmark {

    private String name;

    private String category;

    private String description;

    private String latitude;

    private String longitude;

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public Landmark toLandmark(LandmarkType type) {
        Landmark landmark = new Landmark();
        landmark.setName(name);
        landmark.setDescription(description);
        landmark.setLandmarkType(type);
        landmark.setLatitude(Double.valueOf(latitude));
        landmark.setLongitude(Double.valueOf(longitude));
        landmark.setSource(Source.VISIT);
        return landmark;
    }
}
