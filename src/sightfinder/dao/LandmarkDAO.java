package sightfinder.dao;

import org.springframework.data.repository.CrudRepository;

import sightfinder.model.Landmark;

public interface LandmarkDAO extends CrudRepository<Landmark, Long> {
}
