package sightfinder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sightfinder.dao.LandmarkDAO;
import sightfinder.model.Landmark;
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
}
