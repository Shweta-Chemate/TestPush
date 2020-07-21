package com.cisco.cx.training.app.config;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({ @PropertySource(value = "classpath:environment.properties"),
		@PropertySource(value = "file:/myapp/environment.properties", ignoreResourceNotFound = true) })
public class PropertyConfiguration {
	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${cxpp.elasticsearch.host}")
	private String elasticsearchHost;

	@Value("${elasticsearch.port}")
	private int elasticsearchPort;

	@Value("${elasticsearch.scheme}")
	private String elasticsearchScheme;

	@Value("${elasticsearch.username}")
	private String elasticsearchUsername;

	@Value("${elasticsearch.password}")
	private String elasticsearchPassword;
	
	@Value("${successTalk.elasticsearch.index}")
	private String successTalkIndex;
	
	@Value("${successAcademy.elasticsearch.index}")
	private String successAcademyIndex;
	
	@Value("${successAcademy.elasticsearch.filter.index}")
	private String successAcademyFilterIndex;

	@Value("${smartsheet.successTalk.registration.sheetId}")
	private long successTalkRegistrationSheetId;

	@Value("${smartsheet.accessToken}")
	private String smartsheetAccessToken;

	@Value("${successTalkUserRegistrations.elasticsearch.index}")
	private String successTalkUserRegistrationsIndex;

	@Value("${bookmarks.elasticsearch.index}")
	private String bookmarksIndex;

	@Value("${cxp.basicauth.username}")
	private String cxpBasicAuthUserName;

	@Value("${cxp.basicauth.password}")
	private String cxpBasicAuthPassword;
	
	@Value("${partner.community.heading.partnerresources}")
	private String communityHeading;
	
	@Value("${partner.community.link.partnerresources}")
	private String communityLink;	
	
	@Value("${cxpp.aws.region}")
	private String awsRegion;
	
	@Value("${cxpp.learning.bookmark.table}")
	private String bookmarkTableName;

	public String getCxpBasicAuthUserName() {
		if (StringUtils.isBlank(cxpBasicAuthUserName)) {
			throw new IllegalStateException("CXP Basic Auth Username not present in ENV. Please set cxp_basicauth_username");
		}

		return cxpBasicAuthUserName;
	}

	public String getCxpBasicAuthPassword() {
		if (StringUtils.isBlank(cxpBasicAuthPassword)) {
			throw new IllegalStateException("CXP Basic Auth Password not present in ENV. Please set cxp_basicauth_password");
		}

		return cxpBasicAuthPassword;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getElasticsearchHost() {
		return elasticsearchHost;
	}

	public int getElasticsearchPort() {
		return elasticsearchPort;
	}

	public String getElasticsearchScheme() {
		return elasticsearchScheme;
	}

	public String getElasticsearchUsername() {
		return elasticsearchUsername;
	}

	public String getElasticsearchPassword() {
		return elasticsearchPassword;
	}

	public String getSuccessTalkIndex() {
		return successTalkIndex;
	}

	public String getSuccessAcademyIndex() {
		return successAcademyIndex;
	}
	
	public String getSuccessAcademyFilterIndex() {
		return successAcademyFilterIndex;
	}
	
	public long getSuccessTalkRegistrationSheetId() {
		return successTalkRegistrationSheetId;
	}

	public String getSmartsheetAccessToken() {
		return smartsheetAccessToken;
	}

	public String getSuccessTalkUserRegistrationsIndex() {
		return successTalkUserRegistrationsIndex;
	}

	public String getBookmarksIndex() {
		return bookmarksIndex;
	}
	
	public String getCommunityHeading() {
		return communityHeading;
	}

	public String getCommunityLink() {
		return communityLink;
	}

	public String createCxpBasicAuthToken() {
		return new String(Base64.encodeBase64((this.getCxpBasicAuthUserName() + ":" + this.getCxpBasicAuthPassword()).getBytes()));
	}

	public String getAwsRegion() {
		return awsRegion;
	}

	public String getBookmarkTableName() {
		return bookmarkTableName;
	}

	public void setBookmarkTableName(String bookmarkTableName) {
		this.bookmarkTableName = bookmarkTableName;
	}

}
