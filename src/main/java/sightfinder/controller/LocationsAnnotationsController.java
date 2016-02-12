package sightfinder.controller;

import gate.util.GateException;
import sightfinder.gate.LocationsPipeline;
import sightfinder.model.LandmarkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by krasimira on 11.02.16.
 */
@RestController
@RequestMapping("/annotations")
public class LocationsAnnotationsController {

    @Autowired
    private LocationsPipeline pipeline;

    @RequestMapping(method = RequestMethod.GET)
    public void getAllLandmarkTypes() throws GateException, IOException {
        pipeline.listAnnotations();
    }
}
