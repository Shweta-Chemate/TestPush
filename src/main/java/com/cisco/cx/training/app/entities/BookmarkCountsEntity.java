package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cxpp_learning_bookmark_count")
@IdClass(BookmarkCountsEntityPK.class)
@Data
public class BookmarkCountsEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "puid")
  private String puid;

  @Id
  @Column(name = "learning_item_id")
  private String learningItemId;

  @Column(name = "count")
  private Integer count;
}
