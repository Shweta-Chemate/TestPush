package com.cisco.cx.training.app.dao;

import java.util.Map;
import java.util.Set;

import com.cisco.cx.training.models.BookmarkResponseSchema;

public interface LearningBookmarkDAO {

	BookmarkResponseSchema createOrUpdate(BookmarkResponseSchema bookmarkResponseSchema, String puid);

	Set<String> getBookmarks(String email);

	Map<String,Object> getBookmarksWithTime(String email);

}