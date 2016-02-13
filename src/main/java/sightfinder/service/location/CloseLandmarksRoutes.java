package sightfinder.service.location;

import sightfinder.model.Landmark;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by krasimira on 13.02.16.
 */
public class CloseLandmarksRoutes {



    public void getShortestRoute(Landmark start, Set<Landmark> landmarkGroup) {

        int landmarksCount = landmarkGroup.size();
        landmarkGroup.remove(start);

        Set<Set<Landmark>> landmarksPowerSet = powerSet(landmarkGroup);
        landmarksPowerSet.stream().forEach(set -> set.add(start));

        Map<VisitedLandmarks, Double> routesToLength = new HashMap<>();

        for (int size = 2; size <= landmarksCount; size++) {
            final int finalSize = size;

            Set<Set<Landmark>> setsWithSize = landmarksPowerSet.stream().
                    filter(set -> set.size() == finalSize).collect(Collectors.toSet());

            for (Set<Landmark> subset: setsWithSize) {
                routesToLength.put(new VisitedLandmarks(subset, start), Double.MAX_VALUE);

                subset.stream().forEach(landmark -> {
                    if (!landmark.equals(start)) {
                        Set<Landmark> subsetWithoutLandmark = new HashSet<>(subset);
                        subsetWithoutLandmark.remove(landmark);

                        Double minLength = Double.MAX_VALUE;

                        Iterator<Landmark> setIterator = subsetWithoutLandmark.iterator();

                        while (setIterator.hasNext()) {
                            Landmark visitedLandmark = setIterator.next();
                            VisitedLandmarks visitedLandmarks = new VisitedLandmarks(subsetWithoutLandmark, visitedLandmark);
                            Double length = distanceInKilometers(landmark, visitedLandmark) + routesToLength.get(visitedLandmarks);

                            if (length < minLength) {
                                minLength = length;
                            }
                        }
                    }

                });
            }
        }
    }


//    C({1},1) = 0
//    for s = 2 to n:
//       for all subsets S ⊆ {1,2,...,n} of size s and containing 1:
//           C(S,1) = ∞∞
//           for all j∈S,j≠1:
//               C(S, j) = min{C(S−{j},i)+dij:i∈S,i≠j}
//    return minjC({1,...,n},j)+dj1

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

}
