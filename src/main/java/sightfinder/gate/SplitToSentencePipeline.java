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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SplitToSentencePipeline {

    private static String SENTENCE_ANNOTATION = "Sentence";
   
    private CorpusController corpusController;
    private List<String> documents;

    private static File getPipelineGappFile() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL pipelineResource = classloader.getResource("gate/split-to-sentence-pipeline.gapp");
        File pipelineFile = null;
        try {
            pipelineFile = new File(pipelineResource.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();;
        }
        return pipelineFile;
    }
    
    public List<String> splitToSentences(List<String> documents) throws Exception {
        Gate.init();

        this.documents = documents;
        SplitToSentencePipeline pipeline = getPipelineWithCorpus();
        pipeline.execute();

        List<String> sentences = new ArrayList<String>();
        Corpus annotatedCorpus =  pipeline.corpusController.getCorpus();
        for (int i = 0; i < annotatedCorpus.size(); i++) {
            Document document = annotatedCorpus.get(i);
            for (Annotation annotation : document.getAnnotations()) {
                if (annotation.getType().equals(SENTENCE_ANNOTATION)) {
                    long startOffset = annotation.getStartNode().getOffset();
                    long endOffset = annotation.getEndNode().getOffset();
                    String sentence = document.getContent().getContent(startOffset, endOffset).toString();
                	sentences.add(sentence);
                }
            }
        }

        return sentences;
    }
    
    private void initAnnie() throws Exception {
        File pipelineFile = getPipelineGappFile();
        corpusController = (CorpusController) PersistenceManager.loadObjectFromFile(pipelineFile);
    }

    private void setCorpus(Corpus corpus) {
        corpusController.setCorpus(corpus);
    }

    private void execute() throws GateException {
        Out.prln("Running pipeline...");
        corpusController.execute();
        Out.prln("...pipeline complete");
    }

    private Corpus getDocumentsCorpus() throws GateException {
        Corpus corpus = Factory.newCorpus("Document corpus");
        for (String document: documents) {
            Document landmarkDocument = Factory.newDocument(document);
            corpus.add(landmarkDocument);
        }
        return corpus;
    }

    private SplitToSentencePipeline getPipelineWithCorpus() throws Exception {
    	SplitToSentencePipeline pipeline = new SplitToSentencePipeline();
        pipeline.initAnnie();
        pipeline.setCorpus(getDocumentsCorpus());
        return pipeline;
    }
}