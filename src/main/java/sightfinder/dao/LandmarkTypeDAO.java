package sightfinder.dao;

import org.springframework.data.repository.CrudRepository;

import sightfinder.model.LandmarkType;

public interface LandmarkTypeDAO extends CrudRepository<LandmarkType, Long> {

    LandmarkType findByName(String name);
}
