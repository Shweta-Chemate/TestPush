package com.cisco.cx.training.app.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.cx.training.app.dao.BookmarkDAO;
import com.cisco.cx.training.app.dao.CommunityDAO;
import com.cisco.cx.training.app.dao.LearningDAO;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.app.service.PartnerProfileService;
import com.cisco.cx.training.app.service.TrainingAndEnablementService;
import com.cisco.cx.training.models.BookmarkRequestSchema;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.Community;
import com.cisco.cx.training.models.Learning;
import com.cisco.cx.training.models.LearningModel;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;
import com.cisco.cx.training.models.UserDetails;
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

	@Autowired
	private PartnerProfileService partnerProfileService;

	@Override
	public List<LearningModel> getAllLearning() {
		return learningDAO.getLearnings();
	}

	@Override
	public List<Community> getAllCommunities() {
		return communityDAO.getCommunities();
	}

	@Override
	public SuccessTalkResponseSchema getAllSuccessTalks() {
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successTalkDAO.getAllSuccessTalks());
		return successTalkResponseSchema;
	}

	@Override
	public SuccessTalkResponseSchema getUserSuccessTalks(String xMasheryHandshake) {
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		SuccessTalkResponseSchema successTalkResponseSchema = new SuccessTalkResponseSchema();
		successTalkResponseSchema.setItems(successTalkDAO.getUserSuccessTalks(userDetails.getEmail()));
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
	public SuccesstalkUserRegEsSchema cancelUserSuccessTalkRegistration(String title, Long eventStartDate,
			String xMasheryHandshake) throws IOException {
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		// form a schema object for the input (set transaction type to Canceled)
		SuccesstalkUserRegEsSchema cancelledRegistration = new SuccesstalkUserRegEsSchema(title, eventStartDate,
				userDetails.getEmail(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.CANCELLED);
		try {
			// find and mark registration as Canceled in the Smartsheet
			//smartsheetDAO.cancelUserSuccessTalkRegistration(cancelledRegistration);
			return successTalkDAO.saveSuccessTalkRegistration(cancelledRegistration);
		} catch (Exception se) {
			// log error if smartsheet throws exception and mark it Cancel_Failed for the ES
			// index
			LOG.error("Error while cancelling Success Talk Registration in Smartsheet", se);
			cancelledRegistration.setRegistrationStatus(SuccesstalkUserRegEsSchema.RegistrationStatusEnum.CANCELFAILED);
			successTalkDAO.saveSuccessTalkRegistration(cancelledRegistration);
			throw new GenericException("Error while cancelling Success Talk Registration: " + se.getMessage(), se);
		}
	}

	@Override
	public SuccesstalkUserRegEsSchema registerUserToSuccessTalkRegistration(String title, Long eventStartDate, String xMasheryHandshake) throws Exception {
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		// form a schema object for the input (set transaction type to Pending)
		SuccesstalkUserRegEsSchema registration = new SuccesstalkUserRegEsSchema(title, eventStartDate,
				userDetails.getEmail(), SuccesstalkUserRegEsSchema.RegistrationStatusEnum.PENDING);
		try {
			// validate the registration details
			registration = this.fetchSuccessTalkRegistrationDetails(registration, userDetails);

			if (smartsheetDAO.checkRegistrationExists(registration)) {
				// No Operation as Success Talk is registered already
			} else {
				// save a new row in the smartsheet for this registration
				//smartsheetDAO.saveSuccessTalkRegistration(registration);
			}
			return successTalkDAO.saveSuccessTalkRegistration(registration);
		} catch (SmartsheetException se) {
			// log error if smartsheet throws exception and mark it Register_Failed for the ES index
			LOG.error("Error while saving SuccessTalk Registration in Smartsheet", se);
			registration.setRegistrationStatus(SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERFAILED);
			successTalkDAO.saveSuccessTalkRegistration(registration);
			throw new GenericException("Error while saving SuccessTalk Registration: " + se.getMessage(), se);
		}
	}

	@Override
	public SuccesstalkUserRegEsSchema fetchSuccessTalkRegistrationDetails(SuccesstalkUserRegEsSchema registration,
			UserDetails userDetails) throws NotFoundException, NotAllowedException {
		SuccessTalk successTalk = null;

		try {
			successTalk = successTalkDAO.findSuccessTalk(registration.getTitle(), registration.getEventStartDate());
		} catch (IOException ioe) {
			LOG.error("Could not fetch SuccessTalk details", ioe);
			throw new GenericException("Could not verify if SuccessTalk is valid", ioe);
		}

		if (successTalk != null) {
			registration.setTitle(successTalk.getTitle());
			SuccessTalkSession successTalkSession = successTalk.getSessions().stream().findFirst().get();
			registration.setEventStartDate(successTalkSession.getSessionStartDate());
			registration.setEmail(userDetails.getEmail());
			registration.setFirstName(userDetails.getFirstName());
			registration.setLastName(userDetails.getLastName());
			registration.setUserTitle(userDetails.getTitle());
			registration.setPhone(userDetails.getPhone());
			registration.setCompany(userDetails.getCompany());
			registration.setCountry(userDetails.getCountry());
			registration.setRegistrationDate(new Date().getTime());
		} else {
			throw new NotFoundException("Invalid SuccessTalk Details: " + registration.getTitle());
		}

		return registration;
	}

	@Override
	public BookmarkResponseSchema createOrUpdateBookmark(BookmarkRequestSchema bookmarkRequestSchema,
			String xMasheryHandshake) {
		UserDetails userDetails = partnerProfileService.fetchUserDetails(xMasheryHandshake);
		BookmarkResponseSchema bookmarkResponseSchema = new BookmarkResponseSchema();

		BeanUtils.copyProperties(bookmarkRequestSchema, bookmarkResponseSchema);

		bookmarkResponseSchema.setEmail(userDetails.getEmail());

		bookmarkResponseSchema = bookmarkDAO.createOrUpdate(bookmarkResponseSchema);

		return bookmarkResponseSchema;
	}
}