package sightfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sightfinder.model.Landmark;
import sightfinder.service.LandmarkService;

/**
 * Created by krasimira on 31.01.16.
 */
@RestController
@RequestMapping("/landmarks")
public class LandmarksController {

    @Autowired
    LandmarkService landmarkService;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Landmark> getAllLandmarks() {
        return landmarkService.getLandmarks();
    }

}
