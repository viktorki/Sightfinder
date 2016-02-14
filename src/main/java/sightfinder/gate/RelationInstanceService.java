package sightfinder.gate;

import gate.Annotation;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.util.GateException;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sightfinder.model.Landmark;
import sightfinder.service.LandmarkService;

import com.google.common.collect.Lists;

@Service
public class RelationInstanceService {

    private static final String LOOKUP_ANNOTATION = "Lookup";

    private CorpusController corpusController;

    @Autowired
    private LandmarkService landmarkService;

    private List<Landmark> landmarks;

    public void initAnnie() throws GateException, IOException {
	File pipelineFile = getPipelineGappFile();
	corpusController = (CorpusController) PersistenceManager
		.loadObjectFromFile(pipelineFile);
    }

    public void setCorpus(Corpus corpus) {
	corpusController.setCorpus(corpus);
    }

    public void execute() throws GateException {
	Out.prln("Running pipeline...");
	corpusController.execute();
	Out.prln("...pipeline complete");
    }

    public Corpus getLandmarksCorpus() throws GateException {
	Corpus corpus = Factory.newCorpus("Landmarks corpus");
	for (Landmark landmark : landmarks) {
	    Document landmarkDocument = Factory.newDocument(landmark
		    .getDescription());
	    corpus.add(landmarkDocument);
	}

	return corpus;
    }

    public RelationInstanceService getPipelineWithCorpus()
	    throws GateException, IOException {
	RelationInstanceService pipeline = new RelationInstanceService();
	pipeline.initAnnie();
	pipeline.setCorpus(getLandmarksCorpus());

	return pipeline;
    }

    public List<Annotation> listAnnotations() throws GateException, IOException {
	List<Annotation> annotationList = new ArrayList<Annotation>();

	Gate.init();

	landmarks = Lists.newArrayList(landmarkService.getLandmarks());

	RelationInstanceService pipeline = getPipelineWithCorpus();
	pipeline.execute();

	Corpus annotatedCorpus = pipeline.corpusController.getCorpus();

	for (Document landmarkDocument : annotatedCorpus) {
	    for (Annotation annotation : landmarkDocument.getAnnotations()) {
		if (annotation.getType().equals(LOOKUP_ANNOTATION)
			&& annotation.getFeatures().get("majorType")
				.equals("relation")) {
		    annotationList.add(annotation);
		}
	    }
	}

	return annotationList;
    }

    private static File getPipelineGappFile() {
	ClassLoader classloader = Thread.currentThread()
		.getContextClassLoader();
	URL pipelineResource = classloader
		.getResource("gate/ling-pipe-pipeline-reduced.gapp");
	File pipelineFile = null;
	try {
	    pipelineFile = new File(pipelineResource.toURI());
	} catch (URISyntaxException e) {
	    e.printStackTrace();
	    ;
	}

	return pipelineFile;
    }
}
