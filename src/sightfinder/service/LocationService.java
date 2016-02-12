package sightfinder.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sightfinder.model.Landmark;
import sightfinder.util.Constants;

@Service
public class LocationService {

	@Autowired
	private LandmarkService landmarkService;

	public List<Landmark> getUniqueLandmarksByLocation() {
		Iterable<Landmark> landmarkList = landmarkService.getLandmarks();
		Set<Long> mergedLandmarkIds = new HashSet<Long>();
		List<Landmark> uniqueLandmarkList = new ArrayList<Landmark>();

		for (Landmark landmark : landmarkList) {
			if (!mergedLandmarkIds.contains(landmark.getId())) {
				if (landmark.getLatitude() != null && landmark.getLongitude() != null) {
					List<Landmark> duplicateLandmarkList = landmarkService.findNearestLandmarks(landmark.getLatitude(),
							landmark.getLongitude(), Constants.DISTANCE_ERROR);

					for (Landmark duplicateLandmark : duplicateLandmarkList) {
						if (!landmark.getId().equals(duplicateLandmark.getId())) {
							landmark.getDescription().concat(" ").concat(duplicateLandmark.getDescription());
							mergedLandmarkIds.add(duplicateLandmark.getId());
						}
					}
				}

				uniqueLandmarkList.add(landmark);
			}
		}

		return uniqueLandmarkList;
	}
}
