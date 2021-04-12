package com.cisco.cx.training.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.cx.training.app.dao.NewLearningContentDAO;
import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.app.service.LearningContentService;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTalkSession;

@Service
public class LearningContentServiceImpl implements LearningContentService {

	@Autowired
	private NewLearningContentDAO learningContentDAO;

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

}
