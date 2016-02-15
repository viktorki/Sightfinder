package sightfinder.gate;

import gate.Annotation;
import gate.AnnotationSet;
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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sightfinder.model.Landmark;
import sightfinder.service.LandmarkService;

import com.google.common.collect.Lists;

@Service
public class RelationInstanceService {

	private static final String SENTENCE_ANNOTATION = "Sentence";

	private static final String LOOKUP_ANNOTATION = "Lookup";

	private CorpusController corpusController;

	@Autowired
	private LandmarkService landmarkService;

	private List<Landmark> landmarks;

	public void initAnnie() throws GateException, IOException {
		File pipelineFile = getPipelineGappFile();
		corpusController = (CorpusController) PersistenceManager.loadObjectFromFile(pipelineFile);
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
			Document landmarkDocument = Factory.newDocument(landmark.getDescription());
			corpus.add(landmarkDocument);
		}

		return corpus;
	}

	public RelationInstanceService getPipelineWithCorpus() throws GateException, IOException {
		RelationInstanceService pipeline = new RelationInstanceService();
		pipeline.initAnnie();
		pipeline.setCorpus(getLandmarksCorpus());

		return pipeline;
	}

	public List<Annotation> makeRelations() throws GateException, IOException {
		landmarks = Lists.newArrayList(landmarkService.getLandmarks());

		Corpus annotatedCorpus = getAnnotatedCorpus();

		for (int i = 0; i < annotatedCorpus.size(); i++) {
			Document document = annotatedCorpus.get(i);
			AnnotationSet annotationSet = document.getAnnotations();

			for (Annotation sentenceAnnotation : annotationSet) {
				if (sentenceAnnotation.getType().equals(SENTENCE_ANNOTATION)) {
					Long sentenceStartOffset = sentenceAnnotation.getStartNode().getOffset();
					Long sentenceEndOffset = sentenceAnnotation.getEndNode().getOffset();

					// System.out.print(document.getContent().getContent(sentenceStartOffset,
					// sentenceEndOffset) + " -> ");

					for (Annotation lookupAnnotation : annotationSet) {
						Long lookupAnnotationStartOffset = lookupAnnotation.getStartNode().getOffset();
						Long lookupAnnotationEndOffset = lookupAnnotation.getEndNode().getOffset();

						System.out.println(lookupAnnotation.getFeatures());

						if ((lookupAnnotation.getType().equals(LOOKUP_ANNOTATION))
								&& lookupAnnotationStartOffset >= sentenceStartOffset
								&& lookupAnnotationEndOffset <= sentenceEndOffset) {
							// System.out.print(" "
							// +
							// document.getContent().getContent(lookupAnnotationStartOffset,
							// lookupAnnotationEndOffset));
						}
					}

					// System.out.println();
				}
			}
		}

		return null;
	}

	private Corpus getAnnotatedCorpus() throws GateException, IOException {
		Gate.init();

		RelationInstanceService pipeline = getPipelineWithCorpus();
		pipeline.execute();

		return pipeline.corpusController.getCorpus();
	}

	private static File getPipelineGappFile() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URL pipelineResource = classloader.getResource("gate/make-relation-instances.xgapp");
		File pipelineFile = null;
		try {
			pipelineFile = new File(pipelineResource.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return pipelineFile;
	}
}
