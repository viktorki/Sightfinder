package sightfinder.service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sightfinder.model.Landmark;
import sightfinder.model.MergedLandmark;
import sightfinder.util.ResourceFilesUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by krasimira on 16.02.16.
 */
@Service
public class UniqueLandmarkService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private DBPediaService dbPediaService;
    
    @Autowired
	private IRService informationRetrievalService;

    private List<Landmark> mergedLandmarks;
    private List<MergedLandmark> landmarks;

    public List<MergedLandmark> getUniqueLandmarksOverall() {
        return landmarks;
    }

    public List<Landmark> getUniqueLandmarksMerged() {
        return mergedLandmarks;
    }

    @PostConstruct
    private void init() throws Exception {
        File uniqueLandmarksFile = ResourceFilesUtil.getFileFromResources("calculated/merged-duplication-approaches");

        if (uniqueLandmarksFile == null) {
            landmarks = locationService.getUniqueLandmarksByLocation();
            landmarks = informationRetrievalService.clusterDocuments(landmarks);
            landmarks = informationRetrievalService.summarize(landmarks);
        } else {
            TypeReference<List<MergedLandmark>> typeRef
                    = new TypeReference<List<MergedLandmark>>() {};
            landmarks = new ObjectMapper().readValue(uniqueLandmarksFile, typeRef);
        }

        mergedLandmarks = landmarks.stream()
                .map(mergedLandmark -> mergedLandmark.toLandmark())
                .collect(Collectors.toList());
    }
}
