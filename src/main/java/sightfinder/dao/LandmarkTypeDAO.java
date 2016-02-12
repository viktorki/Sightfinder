package sightfinder.dao;

import org.springframework.data.repository.CrudRepository;

import main.sightfinder.model.LandmarkType;

public interface LandmarkTypeDAO extends CrudRepository<LandmarkType, Long> {

    LandmarkType findByName(String name);
}
