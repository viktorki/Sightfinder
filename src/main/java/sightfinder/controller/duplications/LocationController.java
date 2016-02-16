package sightfinder.controller.duplications;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sightfinder.model.MergedLandmark;
import sightfinder.service.LocationService;

@RestController
@RequestMapping("/location")
public class LocationController {

	@Autowired
	private LocationService locationService;

	@RequestMapping(value = "/landmarks")
	public List<MergedLandmark> getUniqueLandmarksByLocation() {
		return locationService.getUniqueLandmarksByLocation();
	}
}
