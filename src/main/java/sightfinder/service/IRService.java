package sightfinder.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sightfinder.gate.TermsPipeline;
import sightfinder.model.Landmark;
import sightfinder.model.MergedLandmark;
import sightfinder.util.Cluster;
import sightfinder.util.TermDocumentPair;

@Service
public class IRService {
	
//	public static Integer MAX_CLUSTER_SIZE = 10;
	
	@Autowired
	private TermsPipeline termsPipeline;
	
	public List<MergedLandmark> clusterDocuments(Iterable<Landmark> landmarks) throws Exception {
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
						minI = j;
						minJ = i;
					}
				}
			}
//			if (clusters.get(minI).getDocumentids().size() + clusters.get(minJ).getDocumentids().size() >= MAX_CLUSTER_SIZE) {
			if (minCosSimilarity > 0.2) {
				break;
			}
			// Merge cluster with index minI and cluster with minJ
			System.out.printf("Merge %s with %s\n", clusters.get(minI).getDocumentids(), clusters.get(minJ).getDocumentids());
			clusters.get(minI).mergeCluster(clusters.get(minJ));
			clusters.remove(minJ);
			similaritiesMatrix.remove(minJ);
			for (int i = minJ; i < similaritiesMatrix.size(); i++) {
				similaritiesMatrix.get(i).remove(minJ);
			}
			for (int i = 0; i < similaritiesMatrix.get(minI).size(); i++) {
				similaritiesMatrix.get(minI).set(i, clusters.get(i).cosSimilarity(clusters.get(minI)));
			}
			for (int i = minI+1; i < similaritiesMatrix.size(); i++) {
				similaritiesMatrix.get(i).set(minI, clusters.get(i).cosSimilarity(clusters.get(minI)));
			}
		}

		return mergeLandmarks(landmarks, clusters);
	}

	private List<MergedLandmark> mergeLandmarks(Iterable<Landmark> landmarks,
			List<Cluster> clusters) {
		Map<Long, MergedLandmark> landmarksByID = new HashMap<Long, MergedLandmark>();
		for (Landmark landmark: landmarks) {
			landmarksByID.put(landmark.getId(), MergedLandmark.convert(landmark));
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

	private List<List<Double>> fillClustersAndSimilarityMatrix(Iterable<Landmark> landmarks, List<Cluster> clusters, List<List<Double>> similaritiesMatrix) throws Exception {
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
	
	private Map<Long, List<String>> getTermsByDocuments(Iterable<Landmark> landmarks) throws Exception {
		List<Landmark> landmarksArray = StreamSupport.stream(landmarks.spliterator(), false).collect(Collectors.toList());
		Map<Long, String> descriptions = landmarksArray.stream().collect(
				Collectors.toMap(landmark -> (Long) landmark.getId(), landmark -> landmark.getName()));
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
