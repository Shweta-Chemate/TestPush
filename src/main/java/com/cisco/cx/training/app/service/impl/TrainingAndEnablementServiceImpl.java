package com.cisco.cx.training.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.LearningDAO;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.dao.impl.BookmarkDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.Learning;
import com.cisco.cx.training.models.LearningModel;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccessTrackAndUseCases;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.smartsheet.api.SmartsheetException;

@Service
public class TrainingAndEnablementServiceImpl implements TrainingAndEnablementService {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	private CommunityDAO communityDAO;
	
	@Autowired
	private SuccessTalkDAO successTalkDAO;
	
	@Autowired
	private LearningDAO learningDAO;
	
	@Autowired
	private SmartsheetDAO smartsheetDAO;

	@Autowired
	private BookmarkDAO bookmarkDAO;

	@Override
	public SuccessTrackAndUseCases getUsecases() {
		Map<String, List<String>> useCases = new HashMap<String, List<String>>();
		useCases.put("IBN", new ArrayList<>(Arrays.asList("Campus Network Assurance", "Network Device Onboarding",
				"Campus Software Image management", "Campus Network Segmentation", "Scalable Access Policy")));
		SuccessTrackAndUseCases successTrackAndUseCases = new SuccessTrackAndUseCases();
		successTrackAndUseCases.setUseCases(useCases);
		return successTrackAndUseCases;
	}

	@Override
	public List<LearningModel> getAllLearning() {
		return learningDAO.getLearnings();
	}

	@Override
	public Community insertCommunity(Community community) {
		return communityDAO.insertCommunity(community);
	}

	@Override
	public List<Community> getAllCommunities() {
		return communityDAO.getCommunities();
	}

	@Override
	public List<Community> getFilteredCommunities(String solution, String usecase) {
		return communityDAO.getFilteredCommunities(solution, usecase);
	}
	
	@Override
	public SuccessTalk insertSuccessTalk(SuccessTalk successTalk) {
		return successTalkDAO.insertSuccessTalk(successTalk);
	}

	@Override
	public SuccessTalkResponseSchema getAllSuccessTalks() {
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successTalkDAO.getAllSuccessTalks());
		return successTalkResponseSchema;
	}

	@Override
	public SuccessTalkResponseSchema getFilteredSuccessTalks(String solution, String usecase) {
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successTalkDAO.getFilteredSuccessTalks(solution, usecase));
		return successTalkResponseSchema;
	}
	

	@Override
	public SuccessTalkResponseSchema getUserSuccessTalks(String email) {
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successTalkDAO.getUserSuccessTalks(email));
		return successTalkResponseSchema;
	}
	
	@Override
	public Learning insertLearning(Learning learning) {		
		return learningDAO.insertLearning(learning);
	}

	@Override
	public List<LearningModel> getFilteredLearning(String solution, String usecase) {		
		return learningDAO.getFilteredLearnings(solution, usecase);
	}
	
	@Override
	public SuccesstalkUserRegEsSchema cancelUserSuccessTalkRegistration(String title, String email) throws Exception {
		// form a schema object for the input (set transaction type to Canceled)
		SuccesstalkUserRegEsSchema cancelledRegistration = new SuccesstalkUserRegEsSchema(title, email,
				SuccesstalkUserRegEsSchema.RegistrationStatusEnum.CANCELLED);
		try {
			// find and mark registration as Canceled in the Smartsheet
			smartsheetDAO.cancelUserSuccessTalkRegistration(cancelledRegistration);
		} catch (SmartsheetException se) {
			// log error if smartsheet throws exception and mark it Cancel_Failed for the ES index
			LOG.error("Error while cancelling Success Talk Registration in Smartsheet", se);
			cancelledRegistration.setRegistrationStatus(SuccesstalkUserRegEsSchema.RegistrationStatusEnum.CANCELFAILED);
			throw new GenericException("Error while cancelling Success Talk Registration: " + se.getMessage(), se);
		} finally {
			// save the registration object to ES
			return successTalkDAO.saveSuccessTalkRegistration(cancelledRegistration);
		}
	}
	
	@Override
    public SuccesstalkUserRegEsSchema registerUserToSuccessTalkRegistration(String title, String email) throws Exception {
		// form a schema object for the input (set transaction type to Pending)
    	SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema( title, email,
    			SuccesstalkUserRegEsSchema.RegistrationStatusEnum.PENDING);
		try {
			// validate the registration details
			registration = this.fetchSuccessTalkRegistrationDetails(registration);

			if (smartsheetDAO.checkRegistrationExists(registration)) {
				System.out.println("inside registration exists");
				throw new NotAllowedException("Success Talk Registration already exists");
			} else {
				// save a new row in the smartsheet for this registration
				System.out.println("inside registration not exists");
				smartsheetDAO.saveSuccessTalkRegistration(registration);
			}
		} catch (SmartsheetException se) {
			// log error if smartsheet throws exception and mark it Register_Failed for the ES index
			LOG.error("Error while saving SuccessTalk Registration in Smartsheet", se);
			registration.setRegistrationStatus(SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERFAILED);
			throw new GenericException("Error while saving SuccessTalk Registration: " + se.getMessage(), se);
		} finally {
			// save the registration object to ES
			return successTalkDAO.saveSuccessTalkRegistration(registration);
		}
	}

	@Override
	public SuccesstalkUserRegEsSchema fetchSuccessTalkRegistrationDetails(SuccesstalkUserRegEsSchema registration)
			throws NotFoundException, NotAllowedException {
		SuccessTalk successTalk = null;

		try {
			successTalk = successTalkDAO.findSuccessTalk(registration.getTitle());
		} catch (IOException ioe) {
			LOG.error("Could not fetch SuccessTalk details", ioe);
			throw new GenericException("Could not verify if SuccessTalk is valid", ioe);
		}

		if (successTalk != null) {
			registration.setTitle(successTalk.getTitle());
			SuccessTalkSession successTalkSession = successTalk.getSessions().stream().findFirst().get();
			registration.setEventStartDate(successTalkSession.getSessionStartDate());
			//registration.setSessionRegion(atxSessionSchema.getRegion());
			/*try {
				CiscoUserProfileSchema userProfile = profileService.getUserProfile(masheryUser.getCcoId());
				registration.setUserEmail(userProfile.getUserEmail());
				registration.setUserFullName(userProfile.getUserFullName());
				registration.setCustomerName(userProfile.getCompanyName());
			} catch (Exception e) {
				LOG.error("Could not fetch User Profile information from CCO LDAP for " + masheryUser.getCcoId() + ". Some fields may not be available in ATX Registration", e);
			}*/
		} else {
			throw new NotFoundException("Invalid SuccessTalk Details: " + registration.getTitle());
		}

		return registration;
	}
	
	@Override
	public BookmarkResponseSchema createOrUpdateBookmark(BookmarkRequestSchema bookmarkRequestSchema, String email) {
        BookmarkResponseSchema bookmarkResponseSchema = new BookmarkResponseSchema();

        BeanUtils.copyProperties(bookmarkRequestSchema, bookmarkResponseSchema);

        bookmarkResponseSchema.setEmail(email);

        bookmarkResponseSchema = bookmarkDAO.createOrUpdate(bookmarkResponseSchema);

        return bookmarkResponseSchema;
	}
}