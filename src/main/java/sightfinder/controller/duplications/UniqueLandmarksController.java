package sightfinder.controller.duplications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sightfinder.model.Landmark;
import sightfinder.service.DBPediaService;
import sightfinder.service.LocationService;

import java.util.List;

/**
 * Created by krasimira on 13.02.16.
 */

@RestController
@RequestMapping("/unique")
public class UniqueLandmarksController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private DBPediaService dbPediaService;

    @RequestMapping(value = "/landmarks")
    public List<Landmark> getUniqueLandmarksOverall() {
        return locationService.getUniqueLandmarksByLocation(dbPediaService.getUniqueLandmarks());
    }
}
