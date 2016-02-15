package sightfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sightfinder.model.LandmarkType;
import sightfinder.service.LandmarkTypeService;

/**
 * Created by krasimira on 15.02.16.
 */
@RestController
@RequestMapping("/types")
public class TypesController {

    @Autowired
    LandmarkTypeService landmarkTypeService;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<LandmarkType> getAllLandmarkTypes() {
        return landmarkTypeService.getLandmarkTypes();
    }

    @RequestMapping(value = "/{typeId:.*}", method = RequestMethod.DELETE)
    public void deleteLandmarkType(@PathVariable Long typeId) {
        landmarkTypeService.deleteType(typeId);
    }
}
