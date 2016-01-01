package sightfinder.service;

import org.springframework.data.repository.CrudRepository;

import sightfinder.model.LandmarkType;

public interface LandmarkTypeDAO extends CrudRepository<LandmarkType, Long> {

    public LandmarkType findByName(String name);
}
