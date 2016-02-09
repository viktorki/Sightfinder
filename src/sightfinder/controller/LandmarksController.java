package sightfinder.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sightfinder.model.Landmark;
import sightfinder.model.LandmarkType;
import sightfinder.service.DBPediaService;
import sightfinder.service.FacebookService;
import sightfinder.service.LandmarkService;
import sightfinder.service.LandmarkTypeService;

/**
 * Created by krasimira on 31.01.16.
 */
@RestController
@RequestMapping("/landmarks")
public class LandmarksController {
    
    @Autowired
    FacebookService facebookService;
    
    @Autowired
    DBPediaService dbPediaService;

    @Autowired
    LandmarkService landmarkService;

    @Autowired
    LandmarkTypeService landmarkTypeService;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Landmark> getAllLandmarks() {
        return landmarkService.getLandmarks();
    }
    
    @RequestMapping(value = "/types", method = RequestMethod.GET)
    public Iterable<LandmarkType> getAllLandmarkTypes() {
        return landmarkTypeService.getLandmarkTypes();
    }
    
    @RequestMapping(value = "/working-time", method = RequestMethod.POST)
    public List<Landmark> updateWorkingTime() {
    	Iterable<Landmark> landmarks = landmarkService.getLandmarks();
    	List<Landmark> updatedLanmarks = new ArrayList<Landmark>();
    	for (Landmark landmark: landmarks) {
    		if (landmark.getLandmarkType() != null && !landmark.getLandmarkType().hasWorkingTime()) {
    			continue;
    		}
    		
    		try {
				List<String> externalLinks = dbPediaService.fetchExternalLinks(landmark);
				if (externalLinks != null) {
					for (String externalLink: externalLinks) {
						Landmark updatedLandmark = facebookService.updateWorkingTime(externalLink, landmark);
						if (updatedLandmark != null) {
							DateTime from = new DateTime(landmark.getWorkingTimeFrom());
							DateTime to = new DateTime(landmark.getWorkingTimeTo());
							System.out.printf("Found working time for landmark %s: from %s to %s", updatedLandmark.getName(), 
								facebookService.timeFormat.print(from), facebookService.timeFormat.print(to));
							updatedLanmarks.add(updatedLandmark);
							landmarkService.save(updatedLandmark);
							break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
    	}
    	return updatedLanmarks;
    }
}
