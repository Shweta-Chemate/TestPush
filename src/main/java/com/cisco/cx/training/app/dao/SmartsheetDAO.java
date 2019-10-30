package com.cisco.cx.training.app.dao;

import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.smartsheet.api.SmartsheetException;

public interface SmartsheetDAO {

	boolean checkRegistrationExists(SuccesstalkUserRegEsSchema registration) throws SmartsheetException;

	void saveSuccessTalkRegistration(SuccesstalkUserRegEsSchema registration) throws SmartsheetException;

	void cancelUserSuccessTalkRegistration(SuccesstalkUserRegEsSchema cancelledRegistration) throws SmartsheetException;

}