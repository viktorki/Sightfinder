package sightfinder.gate;

import gate.*;
import gate.util.GateException;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;
import sightfinder.model.Landmark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sightfinder.service.UniqueLandmarkService;
import sightfinder.util.ResourseFilesUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by krasimira on 11.02.16.
 */
@Service
public class LocationsPipeline {

    private static final String LOOKUP_ANNOTATION = "Lookup";
    private static final String CLOSENESS_ANNOTATION = "Closeness";
    private static final String LOCATION_FEATURE = "location";

    private static final String CLOSENESS_PIPELINE = "gate/location-closeness.gapp";
    private CorpusController corpusController;

    @Autowired
    private UniqueLandmarkService uniqueLandmarkService;

    private List<Landmark> landmarks;

    public void initPipeline() throws GateException, IOException {
        File pipelineFile = ResourseFilesUtil.getFileFromResources(CLOSENESS_PIPELINE);
        corpusController =
                (CorpusController) PersistenceManager.loadObjectFromFile(pipelineFile);
    }

    public void setCorpus(Corpus corpus) {
        corpusController.setCorpus(corpus);
    }

    public void execute() throws GateException {
        Out.prln("Running pipeline...");
        corpusController.execute();
        Out.prln("...pipeline complete");
    }

    public Corpus getFullCorpus() throws GateException {
        Corpus corpus = Factory.newCorpus("Landmarks corpus");
        for (Landmark landmark : landmarks) {
            Document landmarkDocument = Factory.newDocument(landmark.getDescription());
            corpus.add(landmarkDocument);
        }

        return corpus;
    }

    public Corpus getCorpusForLandmark(Landmark landmark) throws GateException {
        Corpus corpus = Factory.newCorpus("Landmark " + landmark.getId() + " corpus");
        corpus.add(Factory.newDocument(landmark.getDescription()));

        return corpus;
    }

    public LocationsPipeline getPipeline() throws GateException, IOException {
        LocationsPipeline pipeline = new LocationsPipeline();
        pipeline.initPipeline();
        return pipeline;
    }

    public Map<String, Set<Landmark>> listAnnotations() throws GateException, IOException {

        Gate.init();
        landmarks = uniqueLandmarkService.getUniqueLandmarksMerged();

        LocationsPipeline pipeline = getPipeline();
        pipeline.setCorpus(getFullCorpus());
        pipeline.execute();

        Map<String, Set<Landmark>> locationToLandmarks = new HashMap<>();
        Corpus annotatedCorpus =  pipeline.corpusController.getCorpus();

        for (int i = 0; i < annotatedCorpus.size(); i++) {
            Document landmarkDocument = annotatedCorpus.get(i);
            for (Annotation annotation : landmarkDocument.getAnnotations()) {
                if (annotation.getType().equals(CLOSENESS_ANNOTATION)) {
                    String locationToken = (String)annotation.getFeatures().get(LOCATION_FEATURE);
                    if (!locationToLandmarks.containsKey(locationToken)) {
                        locationToLandmarks.put(locationToken, new HashSet<>());
                    }

                    locationToLandmarks.get(locationToken).add(landmarks.get(i));
                }
            }
        }

        return locationToLandmarks;
    }

    public List<String> getLocationsForLandmark(Landmark landmark) throws GateException, IOException {
        LocationsPipeline pipeline = getPipeline();
        pipeline.setCorpus(getCorpusForLandmark(landmark));
        pipeline.execute();

        Corpus annotatedCorpus =  pipeline.corpusController.getCorpus();
        Document landmarkDocument = annotatedCorpus.get(0);
        List<String> locations = new ArrayList<>();

        for (Annotation annotation : landmarkDocument.getAnnotations()) {
            if (annotation.getType().equals(CLOSENESS_ANNOTATION)) {
                String locationToken = (String)annotation.getFeatures().get(LOCATION_FEATURE);
                locations.add(locationToken);
            }
        }

        return locations;
    }

    private String extractTokenFromAnnotation(Document landmarkDocument, Annotation annotation) throws GateException {
        long startOffset = annotation.getStartNode().getOffset();
        long endOffset = annotation.getEndNode().getOffset();
        return landmarkDocument.getContent().
                getContent(startOffset, endOffset).toString();
    }
}
