package sightfinder.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import sightfinder.model.Landmark;
import sightfinder.util.Source;

public interface LandmarkDAO extends CrudRepository<Landmark, Long> {
	Iterable<Landmark> findBySource(Source source);

	@Query("select l from Landmark l where latitude between :minLatitude and :maxLatitude and longitude between :minLongitude and :maxLongitude")
	public List<Landmark> findByCoordinateRange(@Param("minLatitude") Double minLatitude,
			@Param("maxLatitude") Double maxLatitude, @Param("minLongitude") Double minLongitude,
			@Param("maxLongitude") Double maxLongitude);
}
