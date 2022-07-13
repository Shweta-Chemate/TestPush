package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasheryUser {
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(MasheryUser.class);

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  // {"client_cco_id":"krsubbu","party_id":"52428","access_token_uid":"karbala2","access_token_accesslevel":"4"}
  @JsonProperty("party_id")
  private String partyId;

  @JsonProperty("access_token_uid")
  private String ccoId;

  @JsonProperty("access_token_accesslevel")
  private String accessLevel;

  public String getPartyId() {
    return this.partyId;
  }

  public String getCcoId() {
    return this.ccoId;
  }

  public String getAccessLevel() {
    return this.accessLevel;
  }

  private MasheryUser() {}

  public static MasheryUser getInstance(String requestHeader) {
    MasheryUser masheryUser = null;

    if (StringUtils.isNotBlank(requestHeader)) {
      String decodeRequestHeader = new String(Base64.decodeBase64(requestHeader));
      try {
        masheryUser = OBJECT_MAPPER.readValue(decodeRequestHeader, MasheryUser.class);
      } catch (Exception e) {
        throw new IllegalArgumentException("Could not decode Mashery header, User unknown");
      }
    } else {
      throw new IllegalArgumentException("Missing Mashery header, User unknown");
    }

    return masheryUser;
  }

  @Override
  public String toString() {
    return "MasheryUser {"
        + "partyId="
        + partyId
        + ", ccoId="
        + ccoId
        + ", accessLevel="
        + accessLevel
        + '}';
  }
}
