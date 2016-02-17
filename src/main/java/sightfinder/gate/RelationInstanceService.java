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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sightfinder.model.Landmark;
import sightfinder.model.Relation;
import sightfinder.service.LandmarkService;
import sightfinder.service.RelationService;

import com.google.common.collect.Lists;

@Service
public class RelationInstanceService {

	private static final String SENTENCE_ANNOTATION = "Sentence";

	private static final String LOOKUP_ANNOTATION = "Lookup";

	private static final String MAJOR_TYPE_FEATURE = "majorType";

	private static final String RELATION_MAJOR_TYPE = "relation";

	private static final String LOCATION_MAJOR_TYPE = "location";

	private static final String DIMENSION_MAJOR_TYPE = "dimension";

	private static final String UNIT_MAJOR_TYPE = "unit";

	private CorpusController corpusController;

	@Autowired
	private LandmarkService landmarkService;

	@Autowired
	private RelationService relationService;

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

	public List<Relation> getRelations() throws GateException, IOException {
		List<Relation> relationList = new ArrayList<Relation>();
		landmarks = Lists.newArrayList(landmarkService.getLandmarks());

		Corpus annotatedCorpus = getAnnotatedCorpus();

		for (int i = 0; i < annotatedCorpus.size(); i++) {
			Document document = annotatedCorpus.get(i);
			Landmark landmark = landmarks.get(i);
			AnnotationSet annotationSet = document.getAnnotations();

			for (Annotation sentenceAnnotation : annotationSet) {
				if (sentenceAnnotation.getType().equals(SENTENCE_ANNOTATION)) {
					int annotationSetSize = annotationSet.size();
					Long sentenceStartOffset = sentenceAnnotation.getStartNode().getOffset();
					Long sentenceEndOffset = sentenceAnnotation.getEndNode().getOffset();

					for (int j = 0; j < annotationSetSize; j++) {
						Annotation lookupAnnotation = annotationSet.get(j);

						Long lookupAnnotationStartOffset = lookupAnnotation.getStartNode().getOffset();
						Long lookupAnnotationEndOffset = lookupAnnotation.getEndNode().getOffset();
						String lookupAnnotationType = lookupAnnotation.getType();

						if (lookupAnnotationStartOffset >= sentenceStartOffset
								&& lookupAnnotationEndOffset <= sentenceEndOffset
								&& lookupAnnotationType.equals(LOOKUP_ANNOTATION)) {
							if (RELATION_MAJOR_TYPE.equals(lookupAnnotation.getFeatures().get(MAJOR_TYPE_FEATURE))) {
								Relation relation = new Relation();
								relation.setLandmark(landmark);

								relation.setType(document.getContent()
										.getContent(lookupAnnotationStartOffset, lookupAnnotationEndOffset).toString());

								for (int k = j + 1; k < annotationSetSize; k++) {
									Annotation nextAnnotation = annotationSet.get(k);
									Long nextAnnotationStartOffset = nextAnnotation.getStartNode().getOffset();
									Long nextAnnotationEndOffset = nextAnnotation.getEndNode().getOffset();

									if (nextAnnotationStartOffset >= lookupAnnotationEndOffset
											&& nextAnnotationEndOffset <= sentenceEndOffset
											&& nextAnnotation.getType().equals(LOOKUP_ANNOTATION)
											&& nextAnnotation.getFeatures().get(MAJOR_TYPE_FEATURE)
													.equals(LOCATION_MAJOR_TYPE)) {
										relation.setProperties(document.getContent()
												.getContent(nextAnnotationStartOffset, nextAnnotationEndOffset)
												.toString());
										relationList.add(relation);
										relationService.save(relation);

										break;
									}
								}
							} else if (DIMENSION_MAJOR_TYPE.equals(lookupAnnotation.getFeatures().get(
									MAJOR_TYPE_FEATURE))) {
								Relation relation = new Relation();
								relation.setLandmark(landmark);

								relation.setType(document.getContent()
										.getContent(lookupAnnotationStartOffset, lookupAnnotationEndOffset).toString());

								int k;

								for (k = 0; k < annotationSetSize; k++) {
									Annotation nextAnnotation = annotationSet.get(k);
									Long nextAnnotationStartOffset = nextAnnotation.getStartNode().getOffset();
									Long nextAnnotationEndOffset = nextAnnotation.getEndNode().getOffset();

									Pattern p = Pattern.compile("(\\d+)(.*)");

									if (nextAnnotationStartOffset >= sentenceStartOffset
											&& nextAnnotationEndOffset <= sentenceEndOffset
											&& (NumberUtils.isNumber(document.getContent()
													.getContent(nextAnnotationStartOffset, nextAnnotationEndOffset)
													.toString()) || p.matcher(
													document.getContent()
															.getContent(nextAnnotationStartOffset,
																	nextAnnotationEndOffset).toString()).matches())) {
										relation.setProperties(document.getContent()
												.getContent(nextAnnotationStartOffset, nextAnnotationEndOffset)
												.toString());
										relationList.add(relation);
										relationService.save(relation);

										break;
									}
								}

								for (; k < annotationSetSize; k++) {
									Annotation nextAnnotation = annotationSet.get(k);
									Long nextAnnotationStartOffset = nextAnnotation.getStartNode().getOffset();
									Long nextAnnotationEndOffset = nextAnnotation.getEndNode().getOffset();

									if (nextAnnotationStartOffset >= lookupAnnotationEndOffset
											&& nextAnnotationEndOffset <= sentenceEndOffset
											&& UNIT_MAJOR_TYPE.equals(nextAnnotation.getFeatures().get(
													MAJOR_TYPE_FEATURE))) {
										relation.setProperties(relation.getProperties()
												+ " "
												+ document.getContent()
														.getContent(nextAnnotationStartOffset, nextAnnotationEndOffset)
														.toString());

										break;
									}
								}
							}
						}
					}
				}
			}
		}

		return relationList;
	}

	private Corpus getAnnotatedCorpus() throws GateException, IOException {
		Gate.init();

		RelationInstanceService pipeline = getPipelineWithCorpus();
		pipeline.execute();

		return pipeline.corpusController.getCorpus();
	}

	private static File getPipelineGappFile() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URL pipelineResource = classloader.getResource("gate/split-to-sentence-pipeline.gapp");
		File pipelineFile = null;
		try {
			pipelineFile = new File(pipelineResource.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return pipelineFile;
	}
}
