package sightfinder.service.location;

import sightfinder.model.Landmark;

import java.util.Set;

/**
 * Created by krasimira on 13.02.16.
 */
public class VisitedLandmarks {

    private final Set<Landmark> landmarks;
    private final Landmark routeEnd;

    public VisitedLandmarks(Set<Landmark> landmarks, Landmark routeEnd) {
        this.landmarks = landmarks;
        this.routeEnd = routeEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VisitedLandmarks that = (VisitedLandmarks) o;

        if (landmarks != null ? !landmarks.equals(that.landmarks) : that.landmarks != null) return false;
        return !(routeEnd != null ? !routeEnd.equals(that.routeEnd) : that.routeEnd != null);

    }

    @Override
    public int hashCode() {
        int result = landmarks != null ? landmarks.hashCode() : 0;
        result = 31 * result + (routeEnd != null ? routeEnd.hashCode() : 0);
        return result;
    }
}
