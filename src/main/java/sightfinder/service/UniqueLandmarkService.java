package sightfinder.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.scenario.effect.Merge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sightfinder.model.Landmark;
import sightfinder.model.MergedLandmark;
import sightfinder.util.ResourseFilesUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by krasimira on 16.02.16.
 */
@Service
public class UniqueLandmarkService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private DBPediaService dbPediaService;

    private List<Landmark> mergedLandmarks;
    private List<MergedLandmark> landmarks;

    public List<MergedLandmark> getUniqueLandmarksOverall() {
        return landmarks;
    }

    public List<Landmark> getUniqueLandmarksMerged() {
        return mergedLandmarks;
    }

    @PostConstruct
    private void init() throws IOException {
        File uniqueLandmarksFile = ResourseFilesUtil.getFileFromResources("calculated/merged-duplication-approaches");

        if (uniqueLandmarksFile == null) {
            landmarks = getUniqueLandmarksOverall();
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
