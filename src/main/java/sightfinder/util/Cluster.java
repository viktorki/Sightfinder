package sightfinder.util;

import java.util.List;
import java.util.Set;

public class Cluster {

	private Set<Long> documentids;
	private List<Double> tfidfs;
	
	public Set<Long> getDocumentids() {
		return documentids;
	}
	
	public void setDocumentids(Set<Long> documentids) {
		this.documentids = documentids;
	}
	
	public Double cosSimilarity(Cluster cluster) {
		double simililarity = 0;
		for (int i = 0; i < tfidfs.size(); i++) {
			simililarity += tfidfs.get(i) * cluster.tfidfs.get(i);
		}
		return 1 - simililarity;
	}

	public List<Double> getTfidfs() {
		return tfidfs;
	}

	public void setTfidfs(List<Double> tfidfs) {
		this.tfidfs = tfidfs;
	}

	public void normalizeTfIdfVector() {
		double square = 0;
		for (int i = 0; i < tfidfs.size(); i++) {
			square += tfidfs.get(i) * tfidfs.get(i);
		} 
		square = Math.sqrt(square);
		for (int i = 0; i < tfidfs.size(); i++) {
			tfidfs.set(i, tfidfs.get(i) / square);
		} 
	}
	
	public void mergeCluster(Cluster cluster) {
		documentids.addAll(cluster.documentids);
		for (int i = 0; i < tfidfs.size(); i++) {
			tfidfs.set(i, (tfidfs.get(i) + cluster.tfidfs.get(i)) / 2.0);
		}
		normalizeTfIdfVector();
	}
}
