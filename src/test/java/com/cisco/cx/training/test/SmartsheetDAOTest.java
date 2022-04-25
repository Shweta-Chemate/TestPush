package com.cisco.cx.training.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.impl.SmartsheetDAOImpl;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.smartsheet.api.SmartsheetException;

@ExtendWith(SpringExtension.class)
public class SmartsheetDAOTest {
	
	@InjectMocks
	SmartsheetDAOImpl smartsheetDAO;

	@Test
	public void checkRegistrationExists() {
		SuccesstalkUserRegEsSchema surs = new SuccesstalkUserRegEsSchema();
		Assertions.assertThrows( NullPointerException.class , ()->smartsheetDAO.checkRegistrationExists(surs));
	}

	@Test
	public void saveSuccessTalkRegistration() {
		SuccesstalkUserRegEsSchema surs = new SuccesstalkUserRegEsSchema();
		Assertions.assertThrows( NullPointerException.class , ()->smartsheetDAO.saveSuccessTalkRegistration(surs));
	}

	@Test
	public void cancelUserSuccessTalkRegistration() {
		SuccesstalkUserRegEsSchema surs = new SuccesstalkUserRegEsSchema();
		Assertions.assertThrows( NullPointerException.class , ()->smartsheetDAO.cancelUserSuccessTalkRegistration(surs));
	}

}
