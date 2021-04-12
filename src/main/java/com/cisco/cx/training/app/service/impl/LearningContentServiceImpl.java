package com.cisco.cx.training.app.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.dao.SuccessAcademyDAO;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.models.CountResponseSchema;
import com.cisco.cx.training.models.CountSchema;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTalkSession;

@Service
public class LearningContentServiceImpl implements LearningContentService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private NewLearningContentDAO learningContentDAO;

	@Autowired
	private SuccessAcademyDAO successAcademyDAO;
	
	@Override
	public SuccessTalkResponseSchema fetchSuccesstalks() {
		List<NewLearningContentEntity> successTalkEntityList = new ArrayList<NewLearningContentEntity>();
		successTalkEntityList = learningContentDAO.fetchSuccesstalks();
		List<SuccessTalk> successtalkList = new ArrayList<>();
		successtalkList = successTalkEntityList.stream()
				.map(successtalkEntity -> mapLearningEntityToSuccesstalk(successtalkEntity))
				.collect(Collectors.toList());
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successtalkList);
		return successTalkResponseSchema;
	}

	SuccessTalk mapLearningEntityToSuccesstalk(NewLearningContentEntity learningEntity) {
		SuccessTalk successtalk = new SuccessTalk();
		SuccessTalkSession successtalkSession = new SuccessTalkSession();
		List<SuccessTalkSession> sessionList = new ArrayList<>();
		successtalk.setDocId(learningEntity.getId());
		successtalk.setTitle(learningEntity.getTitle());
		successtalk.setDescription(learningEntity.getDescription());
		//Adding Session Details
		successtalkSession.setPresenterName(learningEntity.getPresenterName());
		successtalkSession.setRegion(learningEntity.getRegion());
		successtalkSession.setRegistrationUrl(learningEntity.getRegistrationUrl());
		successtalkSession.setSessionId(learningEntity.getId());
		successtalkSession.setSessionStartDate(learningEntity.getSessionStartDate().getTime());
		successtalkSession.setScheduled(false);
		sessionList.add(successtalkSession);
		successtalk.setSessions(sessionList);
		successtalk.setDuration(learningEntity.getDuration());
		successtalk.setRecordingUrl(learningEntity.getRecordingUrl());
		successtalk.setSuccessTalkId(learningEntity.getId());
		return successtalk;
	}
	
	public List<NewLearningContentEntity> fetchPIWs(String region, String sortField, String sortType, String filter,
			String search) {
		List<NewLearningContentEntity> result = new ArrayList<>();
		try
		{
			Map<String, String> query_map = new LinkedHashMap<String, String>();
			if (!StringUtils.isBlank(filter)) {
				filter = filter.replaceAll("%3B", ";");
				filter = filter.replaceAll("%3A", ":");
				String[] columnFilter = filter.split(";");
				for (int colFilterIndex = 0; colFilterIndex < columnFilter.length; colFilterIndex++) {
					String[] valueFilter = columnFilter[colFilterIndex].split(":");
					String fieldName = valueFilter[0];
					String fieldValue = valueFilter[1];
					query_map.put(fieldName, fieldValue);
				}
			}
			result = learningContentDAO.listPIWs(region, sortField, sortType, query_map, search);

		}catch (Exception e) {
			LOG.error("listByRegion failed: {} ", e);
			throw new GenericException("There was a problem in fetching PIWs by Region.");
		}
		return result;
	}
	
	@Override
	public CountResponseSchema getIndexCounts() {
		LOG.info("Entering the getIndexCounts");
		long requestStartTime = System.currentTimeMillis();		
		List<CountSchema> indexCounts = new ArrayList<>();
		CountResponseSchema countResponse = new CountResponseSchema();
		try {

			CountSchema communityCount= getCommunityCount();
			LOG.info("Received Community count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(communityCount);

			requestStartTime = System.currentTimeMillis();	
			CountSchema successTalkCount = getSuccessTalkCount();
			LOG.info("Received Success talks count in {} ", (System.currentTimeMillis() - requestStartTime));
			indexCounts.add(successTalkCount);
			
			CountSchema successAcamedyCount = new CountSchema();
			successAcamedyCount.setLabel("CX Learning");	
			requestStartTime = System.currentTimeMillis();	
			successAcamedyCount.setCount(successAcademyDAO.count());
			LOG.info("Received Success Academy count in {} ", (System.currentTimeMillis() - requestStartTime));

			indexCounts.add(successAcamedyCount);

			countResponse.setLearningStatus(indexCounts);

		} catch (Exception e) {
			LOG.error("Could not fetch index counts", e);
			throw new GenericException("Could not fetch index counts", e);

		}

		return countResponse;
	}
	
	public CountSchema getCommunityCount() {

		CountSchema communityCount = new CountSchema();
		communityCount.setLabel("Cisco Community");
		// Community Count is currently hardcoded to 1
		communityCount.setCount(1L);
		return communityCount;
	}
	
	public CountSchema getSuccessTalkCount() {

		CountSchema successTalkCount = new CountSchema();
		successTalkCount.setLabel("Success Talks");
		// Success Talks count - Adding filter to exculde cancelled SuccessTalks
		successTalkCount.setCount(new Long(learningContentDAO.getSuccessTalkCount()));
		return successTalkCount;
	}

}
