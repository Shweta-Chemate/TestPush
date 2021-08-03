package com.cisco.cx.training.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.entities.BookmarkCountsEntity;
import com.cisco.cx.training.app.entities.BookmarkCountsEntityPK;

@Repository
public interface BookmarkCountsRepo extends JpaRepository<BookmarkCountsEntity, BookmarkCountsEntityPK> {

	BookmarkCountsEntity findByLearningItemIdAndPuid(String learningItemId, String puid);

}
