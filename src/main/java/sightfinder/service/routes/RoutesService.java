package sightfinder.service.routes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.bcel.internal.generic.LAND;
import gate.util.GateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sightfinder.exception.LandmarkException;
import sightfinder.gate.LocationsPipeline;
import sightfinder.model.Landmark;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by krasimira on 13.02.16.
 */
@Service
public class RoutesService {

    private Map<VisitedLandmarks, BestPosition> routesToLength = new HashMap<>();
    private Map<String, Set<Landmark>> locationsToLandmarks = getLocationsToLandmarks();

    @Autowired
    private LocationsPipeline pipeline;

    public Map<String, List<Landmark>> getShortestRoutes() throws LandmarkException {
        Map<String, List<Landmark>> locationsToRoutes = new HashMap<>();

        for (String location: locationsToLandmarks.keySet()) {
            locationsToRoutes.put(location, getShortestRoute(location));
        }

        return locationsToRoutes;
    }

    public List<Landmark> getShortestRoute(String location) throws LandmarkException {
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

        return getShortestRoute(landmarksInLocation.iterator().next(), landmarksInLocation);
    }



    //    C({1},1) = 0
//    for s = 2 to n:
//       for all subsets S ⊆ {1,2,...,n} of size s and containing 1:
//           C(S,1) = ∞∞
//           for all j∈S,j≠1:
//               C(S, j) = min{C(S−{j},i)+dij:i∈S,i≠j}
//    return minjC({1,...,n},j)+dj1
    private List<Landmark> getShortestRoute(Landmark start, Set<Landmark> landmarkGroup) {
        System.out.println("Starting from:");
        System.out.println(start.getName());

        int landmarksCount = landmarkGroup.size();
        System.out.println("Size is: " + landmarksCount);
        landmarkGroup.remove(start);

        Set<Set<Landmark>> landmarksPowerSet = powerSet(landmarkGroup);
        landmarksPowerSet.stream().forEach(set -> set.add(start));

        System.out.println("Power set size: " + landmarksPowerSet.size());

        landmarkGroup.add(start);

        Set<Landmark> startElementSet = landmarksPowerSet.stream().filter(set -> set.size() == 1).collect(Collectors.toList()).get(0);

        routesToLength.put(new VisitedLandmarks(startElementSet, start), new BestPosition(0, start));

        for (int size = 2; size <= landmarksCount; size++) {
            final int finalSize = size;

            Set<Set<Landmark>> setsWithSize = landmarksPowerSet.stream().
                    filter(set -> set.size() == finalSize).collect(Collectors.toSet());

            System.out.println("Filtered Power set size: " + setsWithSize.size());

            for (Set<Landmark> subset: setsWithSize) {
                routesToLength.put(new VisitedLandmarks(subset, start), new BestPosition(Double.MAX_VALUE, start));

                subset.stream().forEach(landmark -> {
                    System.out.println("For landmark: " + landmark.getName());

                    if (!landmark.equals(start)) {
                        Set<Landmark> subsetWithoutLandmark = new HashSet<>(subset);
                        subsetWithoutLandmark.remove(landmark);

                        Double minLength = Double.MAX_VALUE;
                        Landmark bestPrevious = null;

                        Iterator<Landmark> setIterator = subsetWithoutLandmark.iterator();

                        while (setIterator.hasNext()) {
                            Landmark visitedLandmark = setIterator.next();
                            System.out.println("Visited landmark: " + visitedLandmark.getName());
                            VisitedLandmarks visitedLandmarks = new VisitedLandmarks(subsetWithoutLandmark, visitedLandmark);
                            Double length = distanceInKilometers(landmark, visitedLandmark) +
                                    routesToLength.get(visitedLandmarks).getLength();

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

        return constructRoute(landmarkGroup, start);
    }


    private List<Landmark> constructRoute(Set<Landmark> landmarks, Landmark end) {
        List<Landmark> reversedRoute = new ArrayList<>();
        reversedRoute.add(end);

        while (!landmarks.isEmpty()) {
            BestPosition bestPosition = routesToLength.get(new VisitedLandmarks(landmarks, end));

            Landmark previousLandmark = bestPosition.getComesFrom();
            reversedRoute.add(previousLandmark);

            if (reversedRoute.get(0).equals(previousLandmark)) {
                return reversedRoute;
            }

            landmarks.remove(previousLandmark);
            end = previousLandmark;
        }

        return reversedRoute;
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

    private Map<String, Set<Landmark>> getLocationsToLandmarks() {
        Map<String, Set<Landmark>> locationsToLandmarks = new HashMap<>();
        File groupByLocationResoursesFile = getGroupByLocationResoursesFile();

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

    private static File getGroupByLocationResoursesFile() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL resourseURL = classloader.getResource("gate/group-by-location");
        File locationResoursesFile = null;
        try {
            locationResoursesFile = new File(resourseURL.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return locationResoursesFile;
    }

    public static void main(String[] args) {

//        RoutesService routesService = new RoutesService();
//
//
//
//        routesService.getShortestRoute("Канина");
    }

}
