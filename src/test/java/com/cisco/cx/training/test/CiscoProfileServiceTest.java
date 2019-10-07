package com.cisco.cx.training.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.service.CiscoProfileService;
import com.cisco.cx.training.models.UserProfile;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PropertyConfiguration.class})
public class CiscoProfileServiceTest {
	
	@MockBean
	CiscoProfileService ciscoProfileService;
	
	@Test
	public void getUserProfileTest() {
        UserProfile myUser = new UserProfile();
        myUser.setUserId("1");
        myUser.setFirstName("tanmaya");
        myUser.setLastName("sahoo");
        myUser.setCiscoUid("tasahoo");
        myUser.setMailId("tasahoo@cisco.com");
        myUser.setCountry("India");
        myUser.setAccessLevel("Partner");
        myUser.setUserType("Partner");
        myUser.setMiddleName("kumar");
        myUser.setPhoneNo("8892700110");
        myUser.setNewUser(false);
        
        myUser.getAccessLevel();
        myUser.getCiscoUid();
        myUser.getCountry();
        myUser.getFirstName();
        myUser.getLastName();
        myUser.getMailId();
        myUser.getMiddleName();
        myUser.getPhoneNo();
        myUser.getUserId();
        myUser.getUserType();
        myUser.isNewUser();
        
        myUser.toString();
        
		ciscoProfileService.getUserProfile("tasahoo");
	}
}
