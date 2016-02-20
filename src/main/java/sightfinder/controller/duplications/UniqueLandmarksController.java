package sightfinder.controller.duplications;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sightfinder.model.Landmark;
import sightfinder.model.MergedLandmark;
import sightfinder.service.IRService;
import sightfinder.service.LandmarkService;
import sightfinder.service.UniqueLandmarkService;

/**
 * Created by krasimira on 13.02.16.
 */

@RestController
@RequestMapping("/unique")
public class UniqueLandmarksController {

    @Autowired
    private UniqueLandmarkService uniqueLandmarkService;

    @Autowired
	private IRService informationRetrievalService;
    
    @Autowired
    private LandmarkService landmarkService;
	
	@RequestMapping(value = "/tf-idf/landmarks")
	public Iterable<MergedLandmark> getUniqueLandmarksByTFIDF() {
		try {
			return informationRetrievalService.clusterRawDocuments(landmarkService.getLandmarks());
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<MergedLandmark>();
		}
	}
	
    @RequestMapping(value = "/landmarks")
    public List<Landmark> getUniqueLandmarksMerged() {
        return uniqueLandmarkService.getUniqueLandmarksMerged();
    }
    
    @RequestMapping(value = "/landmarks/all")
    public Iterable<MergedLandmark> getUniqueLandmarksOverall() {
    	return uniqueLandmarkService.getUniqueLandmarksOverall();
    }
}
