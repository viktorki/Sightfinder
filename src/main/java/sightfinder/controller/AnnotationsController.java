package sightfinder.controller;

import gate.Annotation;
import gate.util.GateException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sightfinder.gate.LocationsPipeline;
import sightfinder.gate.RelationInstanceService;
import sightfinder.model.Landmark;

/**
 * Created by krasimira on 11.02.16.
 */
@RestController
@RequestMapping("/annotations")
public class AnnotationsController {

	@Autowired
	private LocationsPipeline pipeline;

	@Autowired
	private RelationInstanceService relationInstanceService;

	@RequestMapping(method = RequestMethod.GET)
	public Map<String, Set<Landmark>> getAllLandmarkTypes() throws GateException, IOException {
		return pipeline.listAnnotations();
	}

	@RequestMapping("/relations")
	public List<Annotation> getRelationAnnotations() throws GateException, IOException {
		return relationInstanceService.makeRelations();
	}
}
