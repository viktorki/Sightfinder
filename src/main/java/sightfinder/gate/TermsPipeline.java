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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Service;

import sightfinder.util.Constants;
import sightfinder.util.Stemmer_UTF8;

@Service
public class TermsPipeline {

    private static String TOKEN_ANNOTATION = "Token";
   
    private CorpusController corpusController;
    private Map<Long, String> documentsByID;
	private Stemmer_UTF8 stemmer = new Stemmer_UTF8();;
	private Set<String> stopWords;
	private Map<Integer, Long> indexToDocumentID = new HashMap<Integer, Long>();

	private static File getPipelineGappFile() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL pipelineResource = classloader.getResource("gate/extract-tokens-pipeline.gapp");
        File pipelineFile = null;
        try {
            pipelineFile = new File(pipelineResource.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();;
        }
        return pipelineFile;
    }
    private void loadStopWords() {
    	stopWords = new HashSet<String>();
    	File stopWordsFile = new File(Thread.currentThread().getContextClassLoader().getResource(Constants.STOP_WORDS_FILE).getFile());
    	try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stopWordsFile), "UTF8"))) {
    	    String line;
    	    while ((line = br.readLine()) != null) {
    	       stopWords.add(line);
    	    }
    	} catch (IOException e) {
			e.printStackTrace();
		}
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
    	indexToDocumentID.clear();
        Corpus corpus = Factory.newCorpus("Document corpus");
        for (Entry<Long, String> entry : documentsByID.entrySet()) {
            Document landmarkDocument = Factory.newDocument(entry.getValue());
            corpus.add(landmarkDocument);
            indexToDocumentID.put(indexToDocumentID.size(), entry.getKey());
        }
        return corpus;
    }

    private TermsPipeline getPipelineWithCorpus() throws Exception {
    	TermsPipeline pipeline = new TermsPipeline();
        pipeline.initAnnie();
        pipeline.setCorpus(getDocumentsCorpus());
        return pipeline;
    }

    public Map<Long, List<String>> tokenizeAndStem(Map<Long, String> documentsByID) throws Exception {
        Gate.init();

        this.documentsByID = documentsByID;
        TermsPipeline pipeline = getPipelineWithCorpus();
        pipeline.execute();

        Map<Long, List<String>> termsByDocument = new HashMap<Long, List<String>>();
        for (Long documentID: documentsByID.keySet()) {
        	termsByDocument.put(documentID, new ArrayList<String>());
        }

        loadStopWords();
    	
		stemmer.loadStemmingRules();
		
        Corpus annotatedCorpus =  pipeline.corpusController.getCorpus();
        for (int i = 0; i < annotatedCorpus.size(); i++) {
            Document landmarkDocument = annotatedCorpus.get(i);
            for (Annotation annotation : landmarkDocument.getAnnotations()) {
                if (annotation.getType().equals(TOKEN_ANNOTATION)) {
                    long startOffset = annotation.getStartNode().getOffset();
                    long endOffset = annotation.getEndNode().getOffset();
                    String token = landmarkDocument.getContent().getContent(startOffset, endOffset).toString();
                    if (!stopWords.contains(token) && token.length() > 2) {
                    	Long documentID = indexToDocumentID.get(i);
                    	termsByDocument.get(documentID).add(stemmer.stem(token));
                    }
                }
            }
        }

        return termsByDocument;
    }
}