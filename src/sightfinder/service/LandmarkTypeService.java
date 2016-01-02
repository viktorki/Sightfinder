package sightfinder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sightfinder.dao.LandmarkTypeDAO;
import sightfinder.model.LandmarkType;

@Service
@Transactional
public class LandmarkTypeService {

    @Autowired
    private LandmarkTypeDAO landmarkTypeDAO;

    public LandmarkType getOrCreate(String name) {
        LandmarkType landmarkType = findByName(name);

        if (landmarkType == null) {
            landmarkType = new LandmarkType();
            landmarkType.setName(name);
            landmarkType = save(landmarkType);
        }

        return landmarkType;
    }

    private LandmarkType save(LandmarkType landmarkType) {
	return landmarkTypeDAO.save(landmarkType);
    }

    private LandmarkType findByName(String name) {
	return landmarkTypeDAO.findByName(name);
    }
}
