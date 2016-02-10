package sightfinder.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class IRService {

	public Double createTFIDF(Map<Long, String> documents, String term, Long documentID) {
		int termFrequency = StringUtils.countMatches(documents.getOrDefault(documentID, ""), term);
		if (termFrequency == 0) {
			return 0.0;
		}
		int inDocuments = 0;
		for (String document: documents.values()) {
			if (StringUtils.countMatches(document, term) > 0) {
				++inDocuments;
			}
		}
		double idf = Math.log10(documents.size() * 1.0 / inDocuments);
		double tf = 1 + Math.log10(termFrequency);
		return idf * tf;
	}

	public Map<String, Map<Long, Integer>> termFrequencies(Map<Long, String> documentsByID) {
		Map<String, Map<Long, Integer>> count_by_document_by_term =  new HashMap<String, Map<Long, Integer>>();
		for (Long documentID: documentsByID.keySet()) {
			for (String word: splitToWords(documentsByID.get(documentID))) {
				Map<Long, Integer> count_by_document = count_by_document_by_term.getOrDefault(word, new HashMap<Long, Integer>());
                count_by_document.put(documentID, count_by_document.getOrDefault(documentID, 0) + 1);
                count_by_document_by_term.put(word, count_by_document);  
			}
		}
		return count_by_document_by_term;
	}
	
	 public String[] splitToWords(String document) {
         return document.split("[ \\\\.\\\\?,!\\\\+\\\\-\\\\/\\\\*\\\\(\\\\)=]+");
	 }
}
