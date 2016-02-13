package sightfinder.controller.duplications;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sightfinder.model.Landmark;
import sightfinder.service.DBPediaService;
import sightfinder.service.LandmarkService;

/**
 * Created by krasimira on 31.01.16.
 */
@RestController
@RequestMapping("/dbpedia")
public class DBPediaController {

    @Autowired
    DBPediaService dbPediaService;
    
    
    @Autowired
    LandmarkService landmarkService;

    @RequestMapping(method = RequestMethod.GET)
    public Map<Long, List<String>> getDBPediaResources() throws IOException {
        return dbPediaService.getDBPediaResources();
    }

    @RequestMapping(value = "/landmarks", method = RequestMethod.POST)
    public List<Landmark> getUniqueLandmarks() {
        return dbPediaService.getUniqueLandmarks();
    }
}
