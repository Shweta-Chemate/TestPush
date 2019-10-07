package com.cisco.cx.training.app.dao;

import java.io.IOException;
import java.util.List;

import com.cisco.cx.training.models.Community;

public interface CommunityDAO {

	public Community insertCommunity(Community community);

	public boolean doesIndexExist(String index) throws IOException;

	public List<Community> getCommunities();

	public List<Community> getFilteredCommunities(String solution, String usecase);
}