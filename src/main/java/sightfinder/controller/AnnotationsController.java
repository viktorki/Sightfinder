package sightfinder.controller;

import gate.util.GateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sightfinder.gate.LocationsPipeline;
import sightfinder.model.Landmark;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by krasimira on 11.02.16.
 */
@RestController
@RequestMapping("/annotations")
public class AnnotationsController {

    @Autowired
    private LocationsPipeline pipeline;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Set<Landmark>> getAllLandmarkTypes() throws GateException, IOException {
        return pipeline.listAnnotations();
    }
}
