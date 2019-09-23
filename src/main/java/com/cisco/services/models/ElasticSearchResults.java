package com.cisco.services.models;

import com.cisco.services.util.HasId;

import java.util.ArrayList;
import java.util.List;

public class ElasticSearchResults<T extends HasId> {
	private List<T> documents = new ArrayList<T>();
	private long totalHits;
	private long count;

	public ElasticSearchResults() {}

	public ElasticSearchResults(long count, long totalHits) {
		this.count = count;
		this.totalHits = totalHits;
	}

	public List<T> getDocuments() {
		return documents;
	}

	public ElasticSearchResults addDocument(T document) {
		this.documents.add(document);
		return this;
	}

	public long getTotalHits() {
		return totalHits;
	}

	public long getCount() {
		return count;
	}
}
