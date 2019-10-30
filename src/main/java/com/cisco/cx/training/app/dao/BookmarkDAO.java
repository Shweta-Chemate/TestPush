package com.cisco.cx.training.app.dao;

import java.util.List;

import com.cisco.cx.training.models.BookmarkResponseSchema;

public interface BookmarkDAO {

	BookmarkResponseSchema createOrUpdate(BookmarkResponseSchema bookmarkResponseSchema);

	List<BookmarkResponseSchema> getBookmarks(String email, String entityId);

}