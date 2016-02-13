package sightfinder.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sightfinder.model.Landmark;
import sightfinder.util.Constants;

/**
 * Created by krasimira on 04.02.16.
 */
@Service
public class DBPediaService {

    @Autowired
    LandmarkService landmarkService;

    private Map<Long, Landmark> landmarks;

    public Map<Long, List<String>> getDBPediaResources() {
        Map<Long, List<String>> resourcesPerLandmark = new HashMap<>();

        File dbPediaResourcesFile = getDBPediaResourcesFile();

        try {
            if (dbPediaResourcesFile == null) {
                resourcesPerLandmark = retrieveResourses();
            } else {
                resourcesPerLandmark =
                        new ObjectMapper().readValue(dbPediaResourcesFile, HashMap.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resourcesPerLandmark;
    }

    private Map<Long, List<String>> retrieveResourses() throws IOException {
        Map<Long, List<String>> resourcesPerLandmark = new HashMap<>();
        for (Landmark landmark: landmarks.values()) {
            long id = landmark.getId();

            if (!resourcesPerLandmark.containsKey(id)) {
                resourcesPerLandmark.put(id, new ArrayList<>());
            }
            resourcesPerLandmark.get(id).addAll(getResources(landmark));
        }

        return resourcesPerLandmark;

    }

    private List<String> getResources(Landmark landmark) throws IOException {
        List<String> landmarkResources = new ArrayList<>();
        Document doc = Jsoup.connect(getDBPediaURL(landmark.getName())).get();
        Element e = doc.getElementById("results");
        if (e != null) {
            Elements resources = e.getElementsByClass("source");
            landmarkResources = resources.stream().map(r -> r.text()).collect(Collectors.toList());
        }

        return landmarkResources;
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

    public List<Landmark> getUniqueLandmarks() {
        Map<Long, List<Long>> possibleDuplicates = findDuplicates(getDBPediaResources());

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
    
    public List<String> fetchExternalLinks(Landmark landmark) throws IOException {
    	List<String> allExternalLinks = new ArrayList<String>();
    	
    	Document doc = Jsoup.connect(getDBPediaURL(landmark.getName())).get();
        Element e = doc.getElementById("results");
        if (e != null) {
            Elements resources = e.getElementsByClass("source");
            if (resources != null) {
            	for (String resource: resources.stream().map(r -> r.getElementsByTag("a").get(0).attr("href")).collect(Collectors.toList())) {
                    List<String> externalLinks = this.getExternalLinksFromResource(resource);
                    allExternalLinks.addAll(externalLinks);
            	}
            }
        }
        
    	return allExternalLinks;
    }
    
    private List<String> getExternalLinksFromResource(String resource) throws IOException {
    	Document doc = Jsoup.connect(Constants.DBPEDIA_URL + resource).get();
        Elements elements = doc.getElementsByClass("even");
        elements.addAll(doc.getElementsByClass("odd"));
        List<String> links = new ArrayList<String>();
        for (Element e: elements) {
        	Elements hrefs = e.getElementsByClass("explicit");
        	if (hrefs.get(0).text().equals(Constants.DBPEDIA_EXTERNAL_LINK)) {
        		links.add(hrefs.get(1).text());
        	}
        }
		return links;
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
            dbpediaUrl = String.format(Constants.DBPEDIA_SEARCH_URL, URLEncoder.encode(landmarkName, "UTF-8"));
            dbpediaUrl += "&_form=%2F";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return dbpediaUrl;
    }

    private static File getDBPediaResourcesFile() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL resourseURL = classloader.getResource("dbpedia/dbpedia-resources");
        File dbpediaResourcesFile = null;
        try {
            dbpediaResourcesFile = new File(resourseURL.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return dbpediaResourcesFile;
    }

    @PostConstruct
    private void getIdsToLandmarks() {
        landmarks = new HashMap<>();
        for (Landmark landmark: landmarkService.getLandmarks()) {
            landmarks.put(landmark.getId(), landmark);
        }
    }
}
