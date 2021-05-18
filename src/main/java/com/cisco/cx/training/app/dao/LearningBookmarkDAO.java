package com.cisco.cx.training.app.dao;

import java.util.Set;

import com.cisco.cx.training.models.BookmarkResponseSchema;

public interface LearningBookmarkDAO {

	BookmarkResponseSchema createOrUpdate(BookmarkResponseSchema bookmarkResponseSchema);

	Set<String> getBookmarks(String email);

	Set<String> getBookmarksWithTime(String email);

}