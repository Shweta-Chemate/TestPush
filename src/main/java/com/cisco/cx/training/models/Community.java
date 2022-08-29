package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;
import lombok.Data;

@Data
public class Community implements HasId {

  private String solution;
  private String usecase;
  private String name;
  private String url;
  private String description;
  private String docId;

  @Override
  public String getDocId() {
    // TODO Auto-generated method stub
    return docId;
  }

  @Override
  public void setDocId(String id) {
    this.docId = id;
  }
}
