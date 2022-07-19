package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PLSResponse {

  private boolean gracePeriod;
  private boolean status;

  @Override
  public String toString() {
    return "PLSResponse [gracePeriod=" + gracePeriod + ", status=" + status + "]";
  }
}
