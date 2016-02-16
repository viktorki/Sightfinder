package sightfinder.util;


public class TermDocumentPair {

	private String term;
	private Long documentID;
	private Double tfidf;
    private Integer countInDocument; // count of occurrences in this document
    private Integer documentsCount; // # documents containing the term
    
	public Integer getDocumentsCount() {
		return documentsCount;
	}

	public void setDocumentsCount(Integer documentsCount) {
		this.documentsCount = documentsCount;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Long getDocumentID() {
		return documentID;
	}

	public void setDocumentID(Long documentID) {
		this.documentID = documentID;
	}

	public Double getTfidf() {
		return tfidf;
	}

	public void setTfidf(Double tfidf) {
		this.tfidf = tfidf;
	}

	public Integer getCountInDocument() {
		return countInDocument;
	}

	public void setCountInDocument(Integer countInDocument) {
		this.countInDocument = countInDocument;
	}
}
