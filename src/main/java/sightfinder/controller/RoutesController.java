package sightfinder.controller;

import gate.util.GateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sightfinder.exception.LandmarkException;
import sightfinder.model.Landmark;
import sightfinder.service.routes.RoutesService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by krasimira on 14.02.16.
 */
@RestController
@RequestMapping("/routes")
public class RoutesController {

    @Autowired
    private RoutesService routesService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, List<Landmark>> getRoutes() throws LandmarkException {
        return routesService.getShortestRoutes();
    }

    @CrossOrigin
    @RequestMapping(value="/{location:.*}", method = RequestMethod.GET)
    public List<Landmark> getRouteForLocations(@PathVariable String location) throws LandmarkException {
        return routesService.getShortestRoute(location);
    }

    @CrossOrigin
    @RequestMapping(value="/landmark/{id:.*}", method = RequestMethod.GET)
    public List<List<Landmark>> getRoutesForLocations(@PathVariable String id) throws GateException, IOException,
            LandmarkException {
        return routesService.getShortestRoutesFrom(Long.valueOf(id));
    }

}
