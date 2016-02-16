package sightfinder.dao;

import org.springframework.data.repository.CrudRepository;

import sightfinder.model.Relation;

public interface RelationDAO extends CrudRepository<Relation, Long> {
}
