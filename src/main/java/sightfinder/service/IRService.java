package sightfinder.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sightfinder.gate.SplitToSentencePipeline;
import sightfinder.gate.TermsPipeline;
import sightfinder.model.Landmark;
import sightfinder.model.MergedLandmark;
import sightfinder.util.Cluster;
import sightfinder.util.Sentence;
import sightfinder.util.TermDocumentPair;

@Service
public class IRService {
	
	private static final double SIMILARITY_THRESHOLD = 0.2;

	public static int MAX_SUMMARY_SENTENCES_COUNT = 5;
	
	private TermsPipeline termsPipeline = new TermsPipeline();
	private SplitToSentencePipeline splitToSentencePipeline = new SplitToSentencePipeline();
	
	public List<MergedLandmark> summarize(List<MergedLandmark> mergedList) throws Exception {
		for (MergedLandmark mergedLandmark: mergedList) {
			mergedLandmark.setDescription(summarize(mergedLandmark.getDescriptions()));
		}
        return mergedList;
	}
	
	public String summarize(Set<String> documents) throws Exception {
		ArrayList<String> documentsList = new ArrayList<String>();
		for (String document: documents) {
			if (document != null) {
				documentsList.add(repair(document));
			}
		}
		List<String> originalSentences = splitToSentencePipeline.splitToSentences(documentsList);

		Map<Long, String> documentsByID = new HashMap<Long, String>();
		for (int i = 0; i < originalSentences.size(); i++) {
			documentsByID.put((long) i, originalSentences.get(i));
		}
		
		Map<String, Long> histogram = new HashMap<String, Long>();
		Map<Long, List<String>> sentencesByWords = termsPipeline.tokenizeAndStem(documentsByID);
		List<Sentence> sentences = new ArrayList<Sentence>();
		int wordsCount = 0;
		for (Entry<Long, List<String>> entry: sentencesByWords.entrySet()) {
			Sentence sentence = new Sentence();
			sentence.setOriginalText(originalSentences.get(entry.getKey().intValue()));
			sentence.setWords(entry.getValue());
			for (String word: entry.getValue()) {
				if (histogram.containsKey(word)) {
					histogram.put(word, histogram.get(word) + 1);
				} else {
					histogram.put(word, (long) 1);
				}
				
			}
			sentences.add(sentence);
			wordsCount += entry.getValue().size();
		}		
		Map<String, Double> distribution = new HashMap<String, Double>();
		for (Entry<String, Long> wordCount: histogram.entrySet()) {
			distribution.put(wordCount.getKey(), wordCount.getValue() * 1.0 / wordsCount);
		}
		StringBuilder summary = new StringBuilder("");
		for (int i = 0; i < MAX_SUMMARY_SENTENCES_COUNT; i++) {
			if (sentences.isEmpty()) {
				return summary.toString();
			}
			calculateScore(sentences, distribution);
			String word = mostLikelyWord(distribution);
			Sentence sentence = highestSentence(sentences, word);
			summary.append(sentence.getOriginalText());
			Double wordLikelyhood = distribution.get(word);
			distribution.put(word, wordLikelyhood * wordLikelyhood);
		}
		
		return summary.toString();
	}

	private String repair(String document) {
		return document.replaceAll("(Св\\.) |(св\\.) |с\\. |гр\\. |р\\. ", "$1").replaceAll("(г.) ([A-Z])", "$1 $2");
	}

	private void calculateScore(List<Sentence> sentences,
			Map<String, Double> distribution) {
		for (Sentence sentence: sentences) {
			double score = 1;
			for (String word: sentence.getWords()) {
				score *= distribution.get(word);
			}
			sentence.setScore(score);
		}
	}
	
	private Sentence highestSentence(List<Sentence> sentences, String word) {
		Sentence maxScoredSentence = null;
		double maxScore = -1;
		for (Sentence sentence: sentences) {
			if (sentence.getWords().contains(word) && sentence.getScore() > maxScore) {
				maxScore = sentence.getScore();
				maxScoredSentence =sentence;
			}
		}
		return maxScoredSentence;
	}

	private String mostLikelyWord(Map<String, Double> distribution) {
		String word = null;
		double maxProbability = -1;
		for (Entry<String, Double> entry: distribution.entrySet()) {
			if (entry.getValue() > maxProbability) {
				word = entry.getKey();
				maxProbability = entry.getValue();
			}
		}
				
		return word;
	}

	public List<MergedLandmark> clusterRawDocuments(Iterable<Landmark> landmarks) throws Exception {
		List<MergedLandmark> mergedLandmarks = new ArrayList<MergedLandmark>();
		for (Landmark landmark: landmarks) {
			mergedLandmarks.add(MergedLandmark.convert(landmark));
		}
		return clusterDocuments(mergedLandmarks);
	}
	
	public List<MergedLandmark> clusterDocuments(Iterable<MergedLandmark> landmarks) throws Exception {
		List<Cluster> clusters = new ArrayList<Cluster>();
		List<List<Double>> similaritiesMatrix = new ArrayList<List<Double>>();
		fillClustersAndSimilarityMatrix(landmarks, clusters, similaritiesMatrix);
		
		while(true) {
			int minI = -1;
			int minJ = -1;
			double minCosSimilarity = 2;
			for (int i = 0; i < clusters.size(); i++) {
				for (int j = 0; j < i; j++) {
					double cosSimilarity = similaritiesMatrix.get(i).get(j);
					if (cosSimilarity < minCosSimilarity) {
						minCosSimilarity = cosSimilarity;
						minI = i;
						minJ = j;
					}
				}
			}
			if (minCosSimilarity > SIMILARITY_THRESHOLD) {
				break;
			}
			// Merge cluster with index minI and cluster with minJ
			clusters.get(minJ).mergeCluster(clusters.get(minI));
			clusters.remove(minI);
			similaritiesMatrix.remove(minI);
			for (int i = minI; i < similaritiesMatrix.size(); i++) {
				similaritiesMatrix.get(i).remove(minI);
			}
			for (int i = 0; i < similaritiesMatrix.get(minJ).size(); i++) {
				similaritiesMatrix.get(minJ).set(i, clusters.get(i).cosSimilarity(clusters.get(minJ)));
			}
			for (int i = minJ+1; i < similaritiesMatrix.size(); i++) {
				similaritiesMatrix.get(i).set(minJ, clusters.get(i).cosSimilarity(clusters.get(minJ)));
			}
		}

		return mergeLandmarks(landmarks, clusters);
	}

	private List<MergedLandmark> mergeLandmarks(Iterable<MergedLandmark> landmarks,
			List<Cluster> clusters) {
		Map<Long, MergedLandmark> landmarksByID = new HashMap<Long, MergedLandmark>();
		for (MergedLandmark landmark: landmarks) {
			landmarksByID.put(landmark.getId(), landmark);
		}
		
		List<MergedLandmark> uniqueLandmarks = new ArrayList<MergedLandmark>();
		for (Cluster cluster: clusters) {
			List<MergedLandmark> mergedLandmarks = cluster.getDocumentids().stream().map(landmarkID -> landmarksByID.get(landmarkID)).collect(Collectors.toList());
			MergedLandmark mergedLandmark = mergedLandmarks.get(0);
			for (int i = 1; i < mergedLandmarks.size(); i++) {
				mergedLandmark.mergeWith(mergedLandmarks.get(i));
			}
			uniqueLandmarks.add(mergedLandmark);
		}
		return uniqueLandmarks;
	}

	private List<List<Double>> fillClustersAndSimilarityMatrix(Iterable<MergedLandmark> landmarks, List<Cluster> clusters, List<List<Double>> similaritiesMatrix) throws Exception {
		Map<Long, List<String>> termsByDocuments = getTermsByDocuments(landmarks);
		Map<String, Map<Long, TermDocumentPair>> tfidf = calculateTFIDF(termsByDocuments);
		for (Long documentID: termsByDocuments.keySet()) {
			Cluster newCluster = new Cluster();
			Set<Long> document = new HashSet<Long>();
			document.add(documentID);
			List<Double> tfidfVector = new ArrayList<Double>();
			for (String term: tfidf.keySet()) {
				if (tfidf.get(term).containsKey(documentID)) {
					tfidfVector.add(tfidf.get(term).get(documentID).getTfidf());
				} else {
					tfidfVector.add(0.0);
				}
			}
			newCluster.setTfidfs(tfidfVector);
			newCluster.setDocumentids(document);
			newCluster.normalizeTfIdfVector();
			clusters.add(newCluster);
			List<Double> similarities = new ArrayList<Double>();
			for (int i = 0; i < clusters.size()-1; i++) {
				similarities.add(newCluster.cosSimilarity(clusters.get(i)));
			}
			similaritiesMatrix.add(similarities);
		}
		return similaritiesMatrix;
	}
	
	public Map<String, Map<Long, TermDocumentPair>> calculateTFIDF(Map<Long, List<String>> termsByDocuments) throws Exception {
		Map<String, HashMap<Long, Integer>> countyByDocumentsByterm =  new HashMap<String, HashMap<Long, Integer>>();
		for (Entry<Long, List<String>> entry: termsByDocuments.entrySet()) {
			for (String word: entry.getValue()) {
				HashMap<Long, Integer> countByDocuments = countyByDocumentsByterm.getOrDefault(word, new HashMap<Long, Integer>());
                countByDocuments.put(entry.getKey(), countByDocuments.getOrDefault(entry.getKey(), 0) + 1);
                countyByDocumentsByterm.put(word, countByDocuments);  
			}
		}
		
		Map<String, Map<Long, TermDocumentPair>> result = new HashMap<String, Map<Long, TermDocumentPair>>();
		for (Entry<String, HashMap<Long, Integer>> entry: countyByDocumentsByterm.entrySet()) {
			HashMap<Long, TermDocumentPair> tfidfPairsByDocument = new HashMap<Long, TermDocumentPair>();
			for (Entry<Long, Integer> countByDocument: entry.getValue().entrySet()) {
				TermDocumentPair termDocumentPair = new TermDocumentPair();
				termDocumentPair.setTerm(entry.getKey());
				termDocumentPair.setDocumentID(countByDocument.getKey());
				termDocumentPair.setCountInDocument(countByDocument.getValue());
				termDocumentPair.setDocumentsCount(entry.getValue().size());
				termDocumentPair.setTfidf(tfidf(termDocumentPair, termsByDocuments.size()));
				tfidfPairsByDocument.put(countByDocument.getKey(), termDocumentPair);
			}
			result.put(entry.getKey(), tfidfPairsByDocument);
		}
		
		return result;
	}
	
	private Map<Long, List<String>> getTermsByDocuments(Iterable<MergedLandmark> landmarks) throws Exception {
		Map<Long, String> descriptions = new HashMap<Long, String>();
		for (MergedLandmark landmark: landmarks) {
			descriptions.put(landmark.getId(), landmark.getDescription());
		}
		return termsPipeline.tokenizeAndStem(descriptions);
	}
	
	private Double tfidf(TermDocumentPair termDocumentPair, int allDocumentsCount) {
		int termFrequency = termDocumentPair.getCountInDocument();
		if (termFrequency == 0) {
			return 0.0;
		}
		int inDocumentsCount = termDocumentPair.getDocumentsCount();
		double idf = Math.log10(allDocumentsCount * 1.0 / inDocumentsCount);
		double tf = 1 + Math.log10(termFrequency);
		return idf * tf;
	}
}
