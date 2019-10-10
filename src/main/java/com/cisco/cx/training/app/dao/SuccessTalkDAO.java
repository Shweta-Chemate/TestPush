package com.cisco.cx.training.app.dao;

import java.util.List;

import com.cisco.cx.training.models.SuccessTalk;

public interface SuccessTalkDAO {

	public SuccessTalk insertSuccessTalk(SuccessTalk successTalk);

	public List<SuccessTalk> getAllSuccessTalks();

	public List<SuccessTalk> getFilteredSuccessTalks(String solution, String usecase);
}