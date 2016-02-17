package sightfinder.service.routes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import gate.util.GateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sightfinder.exception.LandmarkException;
import sightfinder.gate.LocationsPipeline;
import sightfinder.model.Landmark;
import sightfinder.service.LandmarkService;
import sightfinder.util.ResourceFilesUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by krasimira on 13.02.16.
 */
@Service
public class RoutesService {

    private Map<VisitedLandmarks, BestPosition> routesToLength = new HashMap<>();
    private Map<String, Set<Landmark>> locationsToLandmarks = getLocationsToLandmarks();

    private static final int MAX_LOCATION_SIZE = 10;

    @Autowired
    private LocationsPipeline pipeline;

    @Autowired
    private LandmarkService landmarkService;

    public Map<String, List<Landmark>> getShortestRoutes() throws LandmarkException {
        Map<String, List<Landmark>> locationsToRoutes = new HashMap<>();

        for (String location: locationsToLandmarks.keySet()) {
            locationsToRoutes.put(location, getShortestRoute(location));
        }

        return locationsToRoutes;
    }

    public List<Landmark> getShortestRoute(String location) throws LandmarkException {
        Set<Landmark> landmarksInLocation = getLandmarksInLocation(location, null);
        return getShortestRoute(landmarksInLocation.iterator().next(), landmarksInLocation);
    }

    public List<Landmark> getShortestRouteFrom(String location, Landmark startLandmark) throws LandmarkException {
        return getShortestRoute(startLandmark, getLandmarksInLocation(location, startLandmark));
    }

    public List<List<Landmark>> getShortestRoutesFrom(Long landmarkId) throws GateException, IOException, LandmarkException {
        Landmark startLandmark = landmarkService.findLandmarkById(landmarkId);
        if (startLandmark.getLongitude() == null || startLandmark.getLatitude() == null) {
            throw new LandmarkException(String.format("No coordinates for landmark with id %s found", landmarkId));
        }
        List<String> locations = pipeline.getLocationsForLandmark(startLandmark);



        List<List<Landmark>> routes = new ArrayList<>();

        for (String location: locations) {
            routes.add(getShortestRouteFrom(location, startLandmark));
        }

        return routes;
    }

    private List<Landmark> getShortestRoute(Landmark start, Set<Landmark> landmarkGroup) {
        System.out.println("Starting from:");
        System.out.println(start.getName());

        int landmarksCount = landmarkGroup.size();
        System.out.println("Size is: " + landmarksCount);
        landmarkGroup.remove(start);

        Set<Set<Landmark>> landmarksPowerSet = powerSet(landmarkGroup);
        landmarksPowerSet.stream().forEach(set -> set.add(start));
        landmarkGroup.add(start);

        Set<Landmark> startElementSet = landmarksPowerSet.stream().filter(set -> set.size() == 1).collect(Collectors.toList()).get(0);

        routesToLength.put(new VisitedLandmarks(startElementSet, start), new BestPosition(0, start));

        for (int size = 2; size <= landmarksCount; size++) {
            final int finalSize = size;

            Set<Set<Landmark>> setsWithSize = landmarksPowerSet.stream().
                    filter(set -> set.size() == finalSize).collect(Collectors.toSet());
            for (Set<Landmark> subset: setsWithSize) {
                routesToLength.put(new VisitedLandmarks(subset, start), new BestPosition(Double.MAX_VALUE, start));

                subset.stream().forEach(landmark -> {
                    if (!landmark.equals(start)) {
                        System.out.println("For landmark: " + landmark.getName());
                        Set<Landmark> subsetWithoutLandmark = new HashSet<>(subset);
                        subsetWithoutLandmark.remove(landmark);

                        Double minLength = Double.MAX_VALUE;
                        Landmark bestPrevious = null;

                        Iterator<Landmark> setIterator = subsetWithoutLandmark.iterator();

                        while (setIterator.hasNext()) {
                            Landmark visitedLandmark = setIterator.next();
                            System.out.println("Visited landmark: " + visitedLandmark.getName());
                            VisitedLandmarks visitedLandmarks = new VisitedLandmarks(subsetWithoutLandmark, visitedLandmark);

                            System.out.println("Memorized length: " + routesToLength.get(visitedLandmarks).getLength());
                            System.out.println("new Distance: " + distanceInKilometers(landmark, visitedLandmark));
                            Double length = distanceInKilometers(landmark, visitedLandmark) +
                                    routesToLength.get(visitedLandmarks).getLength();
                            System.out.println("Distance form the beginning: " + length);

                            if (length < minLength) {
                                minLength = length;
                                bestPrevious = visitedLandmark;
                            }
                        }

                        routesToLength.put(new VisitedLandmarks(subset, landmark),
                                new BestPosition(minLength, bestPrevious));
                    }

                });
            }
        }
        Landmark end = findBestEnd(start, landmarkGroup);
        return constructRoute(start, end, landmarkGroup);
    }


    private Landmark findBestEnd(Landmark start, Set<Landmark> landmarks) {
        if (landmarks.size() == 1) {
            return start;
        }

        Iterator<Landmark> iterator = landmarks.iterator();
        double minLength = Double.MAX_VALUE;
        Landmark optimalEndLandmark = null;

        while(iterator.hasNext()) {
            Landmark landmark = iterator.next();
            if (!landmark.equals(start)) {
                double length = routesToLength.get(new VisitedLandmarks(landmarks, landmark)).getLength();
                //length += distanceInKilometers(landmark, start);

                if (length < minLength) {
                    minLength = length;
                    optimalEndLandmark = landmark;
                }
            }
        }

        return optimalEndLandmark;
    }


    private List<Landmark> constructRoute(Landmark start, Landmark end, Set<Landmark> landmarks) {
        List<Landmark> reversedRoute = new ArrayList<>();
        reversedRoute.add(start);
        System.out.println("Adding: " + start.getName());
        reversedRoute.add(end);
        System.out.println("Adding: " + end.getName());

        while (!landmarks.isEmpty()) {
            BestPosition bestPosition = routesToLength.get(new VisitedLandmarks(landmarks, end));

            Landmark previousLandmark = bestPosition.getComesFrom();
            if (reversedRoute.get(0).equals(previousLandmark)) {
                return reversedRoute;
            }

            reversedRoute.add(previousLandmark);
            System.out.println("Adding: " + previousLandmark.getName());

            landmarks.remove(end);
            end = previousLandmark;
        }

        return Lists.reverse(reversedRoute);
    }

    /*
        a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
        c = 2 ⋅ atan2( √a, √(1−a) )
        d = R ⋅ c

        where	φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km);
        note that angles need to be in radians to pass to trig functions!
    */
    private static double distanceInKilometers(Landmark first, Landmark second) {

        double firstLatitude = Math.toRadians(first.getLatitude());
        double firstLongitude = Math.toRadians(first.getLongitude());
        double secondLatitude = Math.toRadians(second.getLatitude());
        double secondLongitude = Math.toRadians(second.getLongitude());

        double deltaLatitude = Math.abs(firstLatitude - secondLatitude) / 2;
        double deltaLongitude = Math.abs(firstLongitude - secondLongitude) / 2;

        double a = Math.pow(Math.sin(deltaLatitude), 2) +
                Math.cos(firstLatitude) * Math.cos(secondLatitude) * Math.pow(Math.sin(deltaLongitude), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double radius = 6371;
        return radius * c;
    }

    private static Set<Set<Landmark>> powerSet(Set<Landmark> originalSet) {
        Set<Set<Landmark>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<Landmark> list = new ArrayList<>(originalSet);
        Landmark head = list.get(0);
        Set<Landmark> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<Landmark> set : powerSet(rest)) {
            Set<Landmark> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    private Set<Landmark> getLandmarksInLocation(String location, Landmark startLandmark) throws LandmarkException {
        Set<Landmark> landmarksInLocation = locationsToLandmarks.get(location);
        if (landmarksInLocation == null) {
            throw new LandmarkException(String.format("Location with name %s not found", location));
        }

        landmarksInLocation = landmarksInLocation.stream().filter(landmark ->
                landmark.getLatitude() != null && landmark.getLongitude() != null).collect(Collectors.toSet());

        if (landmarksInLocation.isEmpty()) {
            throw new LandmarkException(String.format(
                    "No landmarks with coordinates fount for location with name %s", location));
        }

        if (landmarksInLocation.size() > MAX_LOCATION_SIZE) {
            landmarksInLocation = new HashSet<>(getMostPopular(landmarksInLocation));
            if (startLandmark != null) {
                landmarksInLocation.add(startLandmark);
            }
        }

        return landmarksInLocation;
    }

    private Map<String, Set<Landmark>> getLocationsToLandmarks() {
        Map<String, Set<Landmark>> locationsToLandmarks = new HashMap<>();
        File groupByLocationResoursesFile = ResourceFilesUtil.getFileFromResources("calculated/group-by-location-new");

        try {
            if (groupByLocationResoursesFile == null) {
                locationsToLandmarks = pipeline.listAnnotations();
            } else {
                TypeReference<HashMap<String, Set<Landmark>>> typeRef
                        = new TypeReference<HashMap<String, Set<Landmark>>>() {};
                locationsToLandmarks = new ObjectMapper().readValue(groupByLocationResoursesFile, typeRef);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GateException e) {
            e.printStackTrace();
        }

        return locationsToLandmarks;
    }

    private static Set<Landmark> getMostPopular(Set<Landmark> landmarksInLocation) {
        List<Landmark> landmarks = new ArrayList<>(landmarksInLocation);
        Collections.sort(landmarks, (l1, l2) ->
                Integer.compare(l1.getPopularity(), l2.getPopularity())
        );

        Set<Landmark> subset = new HashSet<>(landmarks.subList(0, MAX_LOCATION_SIZE));
        return subset;
    }

}
