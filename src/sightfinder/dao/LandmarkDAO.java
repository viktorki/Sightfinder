package sightfinder.dao;

import org.springframework.data.repository.CrudRepository;

import sightfinder.model.Landmark;
import sightfinder.util.Source;

public interface LandmarkDAO extends CrudRepository<Landmark, Long> {
	Iterable<Landmark> findBySource(Source source);
}
