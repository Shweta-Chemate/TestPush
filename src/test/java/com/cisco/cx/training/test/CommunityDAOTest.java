package com.cisco.cx.training.test;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.impl.CommunityDAOImpl;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CommunityDAOTest {
  @Mock private PropertyConfiguration config;

  @InjectMocks private CommunityDAO communityDAO = new CommunityDAOImpl(config);

  @Test
  void getCommunities() throws IOException {
    Assertions.assertNotNull(communityDAO.getCommunities());
  }
}
