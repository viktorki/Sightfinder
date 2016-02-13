package sightfinder.model;

import sightfinder.util.Source;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by krasimira on 13.02.16.
 */
public class MergedLandmark {

    private Set<Long> ids;

    private Set<String> names;

    private Set<LandmarkType> landmarkTypes;

    private Set<Source> sources;

    private Double latitude;

    private Double longitude;

    private Set<String> descriptions;

    private Date workingTimeFrom;

    private Date workingTimeTo;

    private BigDecimal ticketPrice;

    public MergedLandmark() {
        ids = new HashSet<>();
        descriptions = new HashSet<>();
        landmarkTypes = new HashSet<>();
        names = new HashSet<>();
        sources = new HashSet<>();
    }

    public Set<Long> getIds() {
        return ids;
    }

    public Set<LandmarkType> getLandmarkTypes() {
        return landmarkTypes;
    }

    public Set<String> getNames() {
        return names;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Set<String> getDescriptions() {
        return descriptions;
    }

    public Date getWorkingTimeFrom() {
        return workingTimeFrom;
    }

    public void setWorkingTimeFrom(Date workingTimeFrom) {
        this.workingTimeFrom = workingTimeFrom;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Date getWorkingTimeTo() {
        return workingTimeTo;
    }

    public void setWorkingTimeTo(Date workingTimeTo) {
        this.workingTimeTo = workingTimeTo;
    }

    public Set<Source> getSources() {
        return sources;
    }

    public MergedLandmark mergeWith(MergedLandmark second) {

        getIds().addAll(second.getIds());
        getLandmarkTypes().addAll(second.getLandmarkTypes());
        getDescriptions().addAll(second.getDescriptions());
        getNames().addAll(second.getNames());
        getSources().addAll(second.getSources());

        // TODO: maybe check for different values as well?
        if (second.getLongitude() != null)
            setLongitude(second.getLongitude());

        if (second.getLatitude() != null)
            setLatitude(second.getLatitude());

        if (second.getWorkingTimeFrom() != null)
            setWorkingTimeFrom(second.getWorkingTimeFrom());

        if (second.getWorkingTimeTo() != null)
            setWorkingTimeTo(second.getWorkingTimeTo());

        if (second.getTicketPrice() != null)
            setTicketPrice(second.getTicketPrice());

        return this;
    }

    public static MergedLandmark convert(Landmark regularLandmark) {
        MergedLandmark mergedLandmark = new MergedLandmark();

        mergedLandmark.getIds().add(regularLandmark.getId());
        mergedLandmark.getLandmarkTypes().add(regularLandmark.getLandmarkType());
        mergedLandmark.getDescriptions().add(regularLandmark.getDescription());
        mergedLandmark.getNames().add(regularLandmark.getName());
        mergedLandmark.getSources().add(regularLandmark.getSource());

        // TODO: maybe check for different values as well?
        if (regularLandmark.getLongitude() != null)
            mergedLandmark.setLongitude(regularLandmark.getLongitude());

        if (regularLandmark.getLatitude() != null)
            mergedLandmark.setLatitude(regularLandmark.getLatitude());

        if (regularLandmark.getWorkingTimeFrom() != null)
            mergedLandmark.setWorkingTimeFrom(regularLandmark.getWorkingTimeFrom());

        if (regularLandmark.getWorkingTimeTo() != null)
            mergedLandmark.setWorkingTimeTo(regularLandmark.getWorkingTimeTo());

        if (regularLandmark.getTicketPrice() != null)
            mergedLandmark.setTicketPrice(regularLandmark.getTicketPrice());

        return mergedLandmark;
    }

    public boolean contains(Landmark landmark) {
        return getIds().contains(landmark.getId());
    }

    public boolean hasIntersectionWith(List<Landmark> landmarks) {
        for (Landmark landmark: landmarks) {
            if (contains(landmark)) {
                return true;
            }
        }
        return false;
    }

    public Long getGroupId() {
        return ids.iterator().next();
    }
}
