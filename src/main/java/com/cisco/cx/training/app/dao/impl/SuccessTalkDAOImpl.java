package com.cisco.cx.training.app.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.BookmarkDAO;
import com.cisco.cx.training.app.dao.ElasticSearchDAO;
import com.cisco.cx.training.app.dao.SuccessTalkDAO;
import com.cisco.cx.training.app.exception.GenericException;
import com.cisco.cx.training.app.exception.NotAllowedException;
import com.cisco.cx.training.models.BookmarkResponseSchema;
import com.cisco.cx.training.models.ElasticSearchResults;
import com.cisco.cx.training.models.SuccessTalk;
import com.cisco.cx.training.models.SuccessTalkSession;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;

@Repository
public class SuccessTalkDAOImpl implements SuccessTalkDAO{
	
	private static final Logger LOG = LoggerFactory.getLogger(SuccessTalkDAOImpl.class);
	
	private static final String ERROR_MESSAGE = "Error while invoking ES API";

	@Autowired
	private ElasticSearchDAO elasticSearchDAO;
	
    @Autowired
    private PropertyConfiguration config;

	@Autowired
	private BookmarkDAO bookmarkDAO;
	
    public SuccessTalk insertSuccessTalk(SuccessTalk successTalk) {
        // save the entry to ES
        try {
        	successTalk = elasticSearchDAO.saveEntry(config.getSuccessTalkIndex(), successTalk, SuccessTalk.class);
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}
        return successTalk;
    }
    
    public List<SuccessTalk> getAllSuccessTalks(){
    	
		List<SuccessTalk> successTalkES = new ArrayList<>();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<SuccessTalk> results = elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class);

			results.getDocuments().forEach(successTalk -> {
				successTalk.setImageUrl("https://www.cisco.com/web/fw/tools/ssue/cp/lifecycle/atx/images/ATX-DNA-Center-Wireless-Assurance.png");
				successTalk.setRecordingUrl("https://tklcs.cloudapps.cisco.com/tklcs/TKLDownloadServlet?nodeRef=workspace://SpacesStore/cf85fc26-78e0-488e-af04-390fb2c55ad4&activityId=2&fileId=122233");
				successTalk.setDuration(4500L);
				successTalkES.add(successTalk);
			});

		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}

		Collections.sort(successTalkES);
		return successTalkES;

    }
    
	@Override
	public String registerUser(String successTalkSessionId, String successTalkId) {
		try {
			SuccessTalk successTalk= elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class);
			List<SuccessTalkSession> successTalkSessions = successTalk.getSessions();
			successTalkSessions.forEach(session-> {
				if(session.getSessionId().equals(successTalkSessionId))
				{
					this.insertSuccessTalk(successTalk);
				}
			});
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}
		return successTalkId;
	}
	
	@Override
	public String cancelRegistration(String successTalkSessionId, String successTalkId) {
		try {
			SuccessTalk successTalk= elasticSearchDAO.getDocument(config.getSuccessTalkIndex(), successTalkId, SuccessTalk.class);
			List<SuccessTalkSession> successTalkSessions = successTalk.getSessions();
			successTalkSessions.forEach(session-> {
				if(session.getSessionId().equals(successTalkSessionId))
				{
					this.insertSuccessTalk(successTalk);
				}
			});
		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}
		return successTalkId;
	}
	
	@Override
    public SuccesstalkUserRegEsSchema saveSuccessTalkRegistration(SuccesstalkUserRegEsSchema registration) throws IOException {
        // set the updated timestamp of the registration
        registration.setUpdated(System.currentTimeMillis());
        // save the entry to ES
        return elasticSearchDAO.saveEntry(config.getSuccessTalkUserRegistrationsIndex(), registration, SuccesstalkUserRegEsSchema.class);
    }
	
	@Override
    public SuccessTalk findSuccessTalk(String title, Long eventStartDate) throws IOException {
        SuccessTalk matchedSuccessTalk = null;

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchPhraseQuery("title", title));
        sourceBuilder.size(1);
        List<SuccessTalk> matchedSuccessTalkList = elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class).getDocuments();
        if (matchedSuccessTalkList != null && matchedSuccessTalkList.size() > 0) {
        	Optional<SuccessTalk> optionalMatchedSuccessTalkTemp = matchedSuccessTalkList.stream().findFirst();
        	if(optionalMatchedSuccessTalkTemp.isPresent()) {
        	SuccessTalk matchedSuccessTalkTemp = optionalMatchedSuccessTalkTemp.get();
            List<SuccessTalkSession> successTalkSessions = matchedSuccessTalkTemp.getSessions();
            if (successTalkSessions != null && successTalkSessions.size() > 0) {
                List<SuccessTalkSession> matchedSessions = successTalkSessions.stream().filter(session -> session.getSessionStartDate().equals(eventStartDate)).collect(Collectors.toList());
                if (matchedSessions != null && matchedSessions.size() > 0) {
                    List<SuccessTalkSession> futureSessions = matchedSessions.stream()
                            .filter(session -> (session.getSessionStartDate() == null || System.currentTimeMillis() < session.getSessionStartDate()))
                            .collect(Collectors.toList());

                    if (futureSessions == null || futureSessions.size() < 1) {
                        throw new NotAllowedException("Cannot register for session date: " + eventStartDate + " because session start date is in the past");
                    } else {
                        matchedSuccessTalk = matchedSuccessTalkTemp;
                        matchedSuccessTalk.setSessions(futureSessions);
                    }
                }
            }
            matchedSuccessTalk = matchedSuccessTalkTemp;
            }
        }	
        return matchedSuccessTalk;
    }

	@Override
    public List<SuccesstalkUserRegEsSchema> getRegisteredSuccessTalks(String email) {
        List<SuccesstalkUserRegEsSchema> scheduledRegs = new ArrayList<>();

        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = new BoolQueryBuilder();
            QueryBuilder emailQuery = QueryBuilders.matchPhraseQuery("email.keyword", email);
            QueryBuilder transactionType = QueryBuilders.matchPhraseQuery("registrationStatus.keyword", SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED);
            boolQuery.must(emailQuery).must(transactionType);

            sourceBuilder.query(boolQuery);
            sourceBuilder.size(1000);

            ElasticSearchResults<SuccesstalkUserRegEsSchema> results =  elasticSearchDAO.query(config.getSuccessTalkUserRegistrationsIndex(), sourceBuilder, SuccesstalkUserRegEsSchema.class);
            if(results!=null)
            {
            	scheduledRegs = results.getDocuments();
            }
        } catch (IOException ioe) {
            LOG.error(ERROR_MESSAGE, ioe);
            throw new GenericException(ERROR_MESSAGE);
        } catch (Exception e) {
            LOG.error(ERROR_MESSAGE, e);
            throw new GenericException(ERROR_MESSAGE);
        }

        return scheduledRegs;
    }
	
	@Override
	public List<SuccessTalk> getUserSuccessTalks(String email) {
		List<SuccessTalk> successTalkES = new ArrayList<>();
        
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.size(10000);

		try {
			ElasticSearchResults<SuccessTalk> results = elasticSearchDAO.query(config.getSuccessTalkIndex(), sourceBuilder, SuccessTalk.class);

            List<BookmarkResponseSchema> bookmarksList = bookmarkDAO.getBookmarks(email,null);
			List<SuccesstalkUserRegEsSchema> registeredSuccessTalkList = getRegisteredSuccessTalks(email);
			
			results.getDocuments().forEach(successTalk -> {

                for (BookmarkResponseSchema bookmark : bookmarksList) {
                    if (successTalk.getTitle().equalsIgnoreCase(bookmark.getTitle())) {
                    	successTalk.setBookmark(bookmark.isBookmark());
                    }
                }
		        for (SuccesstalkUserRegEsSchema transaction : registeredSuccessTalkList) {
		            if (transaction.getTitle().equalsIgnoreCase(successTalk.getTitle())) {
		            	successTalk.setStatus(SuccessTalk.SuccessTalkStatusEnum.REGISTERED);
		            	successTalk.getSessions().forEach(
		                        session -> {
									if (session.getSessionStartDate().equals(transaction.getEventStartDate())) {
										session.setScheduled(true);
										// if the Attended field in smartsheet is set to Yes, mark it complete.
										if (transaction.getAttendedStatus() != null && transaction.getAttendedStatus()
												.equals(SuccesstalkUserRegEsSchema.AttendedStatusEnum.YES)) {
											successTalk.setStatus(SuccessTalk.SuccessTalkStatusEnum.ATTENDED);
										}
									}
		                        }
		                );
		            }
		        }
				
				
				successTalk.setImageUrl("https://www.cisco.com/web/fw/tools/ssue/cp/lifecycle/atx/images/ATX-DNA-Center-Wireless-Assurance.png");
				successTalk.setDuration(4500L);
				successTalkES.add(successTalk);
			});

		} catch (IOException ioe) {
			LOG.error(ERROR_MESSAGE, ioe);
			throw new GenericException(ERROR_MESSAGE);
		}

		Collections.sort(successTalkES);
		return successTalkES;
	}

}