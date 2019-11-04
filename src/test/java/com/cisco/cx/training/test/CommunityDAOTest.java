package com.cisco.cx.training.test;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.impl.CommunityDAOImpl;

@RunWith(SpringRunner.class)
public class CommunityDAOTest {
	@Mock
	private PropertyConfiguration config;

	@InjectMocks
	private CommunityDAO communityDAO = new CommunityDAOImpl();

	@Test
	public void getCommunities() throws IOException {
		communityDAO.getCommunities();
	}

}
