package com.cisco.cx.training.app.entities;

import java.io.Serializable;
import lombok.Data;

@Data
public class PeerViewedEntityPK implements Serializable {

  private static final long serialVersionUID = 2964849291358013593L;

  private String cardId;
  private String roleName;
}
