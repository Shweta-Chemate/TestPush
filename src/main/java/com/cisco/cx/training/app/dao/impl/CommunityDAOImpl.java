package com.cisco.cx.training.app.dao.impl;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.models.Community;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityDAOImpl implements CommunityDAO {
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(CommunityDAOImpl.class);

  private PropertyConfiguration config;

  @Autowired
  public CommunityDAOImpl(PropertyConfiguration config) {
    this.config = config;
  }

  public List<Community> getCommunities() {
    Community community = new Community();
    community.setDescription("");
    community.setName(config.getCommunityHeading());
    community.setSolution("IBN");
    community.setUrl(config.getCommunityLink());
    community.setUsecase("Campus Network Assurance");
    return Arrays.asList(community);
  }
}
