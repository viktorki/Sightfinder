package sightfinder.gate;

import gate.*;
import gate.util.GateException;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;
import sightfinder.model.Landmark;
import sightfinder.service.LandmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by krasimira on 11.02.16.
 */
@Service
public class LocationsPipeline {

    private static String LOOKUP_ANNOTATION = "Lookup";
    private CorpusController corpusController;

    @Autowired
    private LandmarkService landmarkService;

    private Iterable<Landmark> landmarks;

    public void initAnnie() throws GateException, IOException {
        File pipelineFile = getPipelineGappFile();
        corpusController =
                (CorpusController) PersistenceManager.loadObjectFromFile(pipelineFile);
    }


    public void setCorpus(Corpus corpus) {
        corpusController.setCorpus(corpus);
    }

    public void execute() throws GateException {
        Out.prln("Running ANNIE...");
        corpusController.execute();
        Out.prln("...ANNIE complete");
    }

    public Corpus getLandmarksCorpus() throws GateException {
        Corpus corpus = Factory.newCorpus("Landmarks corpus");


        int count = 0;
        for (Landmark landmark : landmarks) {
            if (count++ < 10) {
                Document landmarkDocument = Factory.newDocument(landmark.getDescription());
                corpus.add(landmarkDocument);
            } else {
                break;
            }
        }

        return corpus;
    }

    public LocationsPipeline getPipelineWithCorpus() throws GateException, IOException {
        LocationsPipeline pipeline = new LocationsPipeline();
        pipeline.initAnnie();
        pipeline.setCorpus(getLandmarksCorpus());

        return pipeline;
    }

    public void listAnnotations() throws GateException, IOException {

        Gate.init();

        landmarks = landmarkService.getLandmarks();

        LocationsPipeline pipeline = getPipelineWithCorpus();
        pipeline.execute();

        Iterator annotatedLandmarksIterator = pipeline.corpusController.getCorpus().iterator();
        Map<String, List<Landmark>> locationToLandmarks = new HashMap<>();

        while (annotatedLandmarksIterator.hasNext()) {
            Document landmarkDocument = (Document) annotatedLandmarksIterator.next();
            for (Annotation annotation : landmarkDocument.getAnnotations()) {
                if (annotation.getType().equals(LOOKUP_ANNOTATION)) {
                    long startOffset = annotation.getStartNode().getOffset();
                    long endOffset = annotation.getEndNode().getOffset();
                    String locationToken = landmarkDocument.getContent().getContent(startOffset, endOffset).toString();

                    if (!locationToLandmarks.containsKey(locationToken)) {
                        locationToLandmarks.put(locationToken, new ArrayList<>());
                    }

                    //locationToLandmarks.get(locationToken).add(landmarkDocument)
                    System.out.println(locationToken);
                }
            }
        }

        System.out.print("The end!!");
    }

    private static File getPipelineGappFile() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL pipelineResource = classloader.getResource("gate/ling-pipe-pipeline-reduced.gapp");
        File pipelineFile = null;
        try {
            pipelineFile = new File(pipelineResource.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();;
        }

        return pipelineFile;
    }
}
