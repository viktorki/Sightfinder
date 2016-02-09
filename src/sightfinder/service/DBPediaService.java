package sightfinder.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sightfinder.model.Landmark;
import sightfinder.util.Constants;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by krasimira on 04.02.16.
 */
@Service
public class DBPediaService {

    @Autowired
    LandmarkService landmarkService;

    private Map<Long, Landmark> landmarks;



    public Map<Long, List<String>> getDBPediaResources() throws IOException {
        Map<Long, List<String>> resourcesPerLandmark = new HashMap<>();
        for (Landmark landmark: landmarks.values()) {
            long id = landmark.getId();

            if (!resourcesPerLandmark.containsKey(id)) {
                resourcesPerLandmark.put(id, new ArrayList<>());
            }

            Document doc = Jsoup.connect(getDBPediaURL(landmark.getName())).get();
            Element e = doc.getElementById("results");
            if (e != null) {
                Elements resources = e.getElementsByClass("source");
                resourcesPerLandmark.get(id).addAll(
                        resources.stream().map(r -> r.text()).collect(Collectors.toList()));
            }
        }

        return resourcesPerLandmark;
    }

    public Map<Long, List<Long>> findDuplicates(Map<Long, List<String>> landmarksResources) {
        Map<Long, List<Long>> possibleDuplicates = new HashMap<>();
        for (Long id: landmarksResources.keySet()) {
            Set<String> resources = new HashSet(landmarksResources.get(id));
            if (!possibleDuplicates.containsKey(id)) {
                possibleDuplicates.put(id, new ArrayList<>());
            }
            for (Long anotherId: landmarksResources.keySet()) {
                Set<String> anotherResources = new HashSet(landmarksResources.get(anotherId));
                if (!anotherResources.isEmpty() && resources.containsAll(anotherResources) &&
                        anotherResources.containsAll(resources)) {
                    possibleDuplicates.get(id).add(anotherId);
                }
            }
        }

        return possibleDuplicates;
    }

    public List<Landmark> getUniqueLandmarks(Map<Long, List<String>> landmarksResources) {
        Map<Long, List<Long>> possibleDuplicates = findDuplicates(landmarksResources);

        List<Landmark> uniqueLandmarks = new ArrayList<>();
        List<Long> includedLandmarks = new ArrayList<>();

        for (Long id: possibleDuplicates.keySet()) {
            List<Long> relatedLandmarks = possibleDuplicates.get(id);

            if (!includedLandmarks.contains(id)) {
                Landmark mergedLandmark = mergeLandmarks(id, relatedLandmarks);
                uniqueLandmarks.add(mergedLandmark);
                includedLandmarks.addAll(relatedLandmarks);
            }
        }

        return uniqueLandmarks;
    }
    
    private Landmark mergeLandmarks(long landmsrkId, List<Long> relatedLandmarksIds) {
        Landmark mergedLandmark = landmarks.get(landmsrkId);
        if (relatedLandmarksIds.size() > 0) {
            mergedLandmark = relatedLandmarksIds.stream().
                    map(id -> landmarks.get(id)).
                    reduce((landmark1, landmark2) -> (landmark1.mergeWith(landmark2))).get();
        }

        return mergedLandmark;
    }

    private static String getDBPediaURL(String landmarkName) {
        String dbpediaUrl = null;
        try {
            dbpediaUrl = String.format(Constants.DBPEDIA_URL, URLEncoder.encode(landmarkName, "UTF-8"));
            dbpediaUrl += "&_form=%2F";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return dbpediaUrl;
    }

    @PostConstruct
    private void getIdsToLandmarks() {
        landmarks = new HashMap<>();
        for (Landmark landmark: landmarkService.getLandmarks()) {
            landmarks.put(landmark.getId(), landmark);
        }
    }
}
