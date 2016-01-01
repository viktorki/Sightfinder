package sightfinder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sightfinder.model.LandmarkType;

@Service
@Transactional
public class LandmarkTypeService {

    @Autowired
    private LandmarkTypeDAO landmarkTypeDAO;

    public LandmarkType save(LandmarkType landmarkType) {
	return landmarkTypeDAO.save(landmarkType);
    }

    public LandmarkType findByName(String name) {
	return landmarkTypeDAO.findByName(name);
    }
}
