package com.cisco.cx.training.app.dao;

import java.io.IOException;
import java.util.List;

import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;

public interface SuccessTalkDAO {

	public SuccessTalk insertSuccessTalk(SuccessTalk successTalk);

	public List<SuccessTalk> getAllSuccessTalks();

	public List<SuccessTalk> getFilteredSuccessTalks(String solution, String usecase);
	
	public List<SuccessTalk> getUserSuccessTalks(String email);

	public String registerUser(String successTalkSessionId, String successTalkId);

	public String cancelRegistration(String successTalkSessionId, String successTalkId);
	
	public SuccesstalkUserRegEsSchema saveSuccessTalkRegistration(SuccesstalkUserRegEsSchema registration) throws Exception;
	
	public SuccessTalk findSuccessTalk(String title) throws IOException;
	
	public List<SuccesstalkUserRegEsSchema> getRegisteredSuccessTalks(String email);
}