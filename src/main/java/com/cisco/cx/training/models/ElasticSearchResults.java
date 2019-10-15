package com.cisco.cx.training.models;

import java.util.ArrayList;
import java.util.List;

import com.cisco.cx.training.util.HasId;

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

	public ElasticSearchResults<T> addDocument(T document) {
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
