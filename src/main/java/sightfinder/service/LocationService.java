package sightfinder.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sightfinder.model.Landmark;
import sightfinder.model.MergedLandmark;
import sightfinder.util.Constants;

import com.google.common.collect.Lists;

@Service
public class LocationService {

	@Autowired
	private LandmarkService landmarkService;

	public List<MergedLandmark> getUniqueLandmarksByLocation() {
		Iterable<Landmark> landmarkList = landmarkService.getLandmarks();

		List<MergedLandmark> mergedList = Lists.newArrayList(landmarkList).stream().map(landmark -> MergedLandmark.convert(landmark)).collect(Collectors.toList());
		return getUniqueLandmarksByLocation(mergedList);
	}

	public List<MergedLandmark> getUniqueLandmarksByLocation(List<MergedLandmark> landmarkList) {
		Set<Long> mergedLandmarkIds = new HashSet<>();
		List<MergedLandmark> uniqueLandmarkList = new ArrayList<>();

		for (MergedLandmark landmark : landmarkList) {
			if (!mergedLandmarkIds.contains(landmark.getGroupId())) {
				mergedLandmarkIds.addAll(landmark.getIds());
				if (landmark.getLatitude() != null && landmark.getLongitude() != null) {
					List<Landmark> duplicateLandmarkList = landmarkService.findNearestLandmarks(landmark.getLatitude(),
							landmark.getLongitude(), Constants.DISTANCE_ERROR);

					for (MergedLandmark secondLandmarks : landmarkList) {
						if (secondLandmarks.hasIntersectionWith(duplicateLandmarkList)) {
							landmark.mergeWith(secondLandmarks);
							mergedLandmarkIds.addAll(secondLandmarks.getIds());
						}
					}
				}

				uniqueLandmarkList.add(landmark);
			}
		}

		return uniqueLandmarkList;
	}
}