package sightfinder.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sightfinder.dao.RelationDAO;
import sightfinder.model.Relation;

@Service
@Transactional
public class RelationService {

	@Autowired
	private RelationDAO relationDAO;

	public void save(Relation relation) {
		relationDAO.save(relation);
	}
}
