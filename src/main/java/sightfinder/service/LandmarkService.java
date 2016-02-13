package sightfinder.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sightfinder.dao.LandmarkDAO;
import sightfinder.model.Landmark;
import sightfinder.util.Constants;
import sightfinder.util.Source;

@Service
@Transactional
public class LandmarkService {

	@Autowired
	private LandmarkDAO landmarkDAO;

	public Landmark save(Landmark landmark) {
		return landmarkDAO.save(landmark);
	}

	public Iterable<Landmark> getLandmarks() {
		return landmarkDAO.findAll();
	}

	public Landmark findLandmarkById(Long id) {
		return landmarkDAO.findOne(id);
	}

	public Iterable<Landmark> findLandmarksBySource(Source source) {
		return landmarkDAO.findBySource(source);
	}

	public List<Landmark> findNearestLandmarks(Double latitude, Double longitude, Long maxDistance) {
		Double difference = 180 * maxDistance / (Math.PI * Constants.RADIUS_OF_EARTH);
		List<Landmark> landmarksWithNearestCoordinates = landmarkDAO.findByCoordinateRange(latitude - difference,
				latitude + difference, longitude - difference, longitude + difference);
		List<Landmark> nearestLandmarks = new ArrayList<Landmark>();

		for (Landmark landmark : landmarksWithNearestCoordinates) {
			Double distance = getDistance(latitude, longitude, landmark.getLatitude(), landmark.getLongitude());

			if (distance <= maxDistance) {
				landmark.setDistance(distance);
				nearestLandmarks.add(landmark);
			}
		}

		nearestLandmarks.sort(Landmark.LandmarkDistanceComparator);

		return nearestLandmarks;
	}

	private Double getDistance(Double latitude1, Double longitude1, Double latitude2, Double longitude2) {
		Double fi1 = Math.toRadians(latitude1);
		Double fi2 = Math.toRadians(latitude2);
		Double dfi = Math.toRadians(latitude2 - latitude1);
		Double dlambda = Math.toRadians(longitude2 - longitude1);
		Double a = Math.sin(dfi / 2) * Math.sin(dfi / 2) + Math.cos(fi1) * Math.cos(fi2) * Math.sin(dlambda / 2)
				* Math.sin(dlambda / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return Constants.RADIUS_OF_EARTH * c;
	}
}
