package com.cisco.cx.training.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class SuccessTipsAttachment implements Serializable {

  /** */
  private static final long serialVersionUID = -5038905336450032758L;

  private String url;

  private String urlDescription;

  private String urlTitle;

  private String attachmentType;
}
