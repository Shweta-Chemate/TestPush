package com.cisco.cx.training.app.entities;

import java.io.Serializable;

public class PeerViewedEntityPK implements Serializable {

  private static final long serialVersionUID = 2964849291358013593L;

  private String cardId;
  private String roleName;

  public String getCardId() {
    return cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public String getRole_name() {
    return roleName;
  }

  public void setRoleName(String role_name) {
    this.roleName = role_name;
  }
}
