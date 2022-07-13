package com.cisco.cx.training.app.repo;

import com.cisco.cx.training.app.entities.BookmarkCountsEntity;
import com.cisco.cx.training.app.entities.BookmarkCountsEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkCountsRepo
    extends JpaRepository<BookmarkCountsEntity, BookmarkCountsEntityPK> {

  BookmarkCountsEntity findByLearningItemIdAndPuid(String learningItemId, String puid);
}
