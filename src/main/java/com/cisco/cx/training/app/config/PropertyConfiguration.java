package com.cisco.cx.training.app.config;

import javax.crypto.SealedObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySource(value = "classpath:environment.properties")
@PropertySource(value = "file:/myapp/environment.properties", ignoreResourceNotFound = true) 
public class PropertyConfiguration {
	
	private final CryptoAccess<String> cryptoAccess = new CryptoAccess<>();
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Value("${partner.community.heading.partnerresources}")
	private String communityHeading;
	
	@Value("${partner.community.link.partnerresources}")
	private String communityLink;
	
	private SealedObject successTalkIndex;

	private SealedObject successAcademyIndex;

	private SealedObject successAcademyFilterIndex;

	private SealedObject successTalkRegistrationSheetId;

	private SealedObject smartsheetAccessToken;

	private SealedObject successTalkUserRegistrationsIndex;

	private SealedObject bookmarksIndex;

	private SealedObject cxpBasicAuthUserName;

	private SealedObject cxpBasicAuthPassword;
	
	private SealedObject awsRegion;
	
	private String bookmarkTableName;
 
	private SealedObject rbacExcludedEndPoints;

	private SealedObject rbacIncludedEndPoints;

	private SealedObject authUrl;

	private SealedObject partnerUserDetails;
	
	@Value("${0.9.5.learning.feature}")
	private boolean newLearningFeature;	
	
	@Value("${cxpp.user.learning.preferences.table}")
	private String ulPreferencesTableName;

	@Value("${get.partner.status}")
	private String plsURL;

	public String getPartnerUserDetails() {
		return cryptoAccess.unseal(partnerUserDetails);
	}

	@Value("${cxpp.partner.user.details}")
	public void setPartnerUserDetails(String partnerUserDetails) {
		this.partnerUserDetails = cryptoAccess.seal(partnerUserDetails);
	}
	
	public String getCxpBasicAuthUserName() {
		if (StringUtils.isBlank(cryptoAccess.unseal(cxpBasicAuthUserName))) {
			throw new IllegalStateException("CXP Basic Auth Username not present in ENV. Please set cxp_basicauth_username");
		}

		return cryptoAccess.unseal(cxpBasicAuthUserName);
	}
	
	@Value("${cxp.basicauth.username}")
	public void setCxpBasicAuthUserName(String cxpBasicAuthUserName) {
		this.cxpBasicAuthUserName = cryptoAccess.seal(cxpBasicAuthUserName);
	}

	public String getCxpBasicAuthPassword() {
		if (StringUtils.isBlank(cryptoAccess.unseal(cxpBasicAuthPassword))) {
			throw new IllegalStateException("CXP Basic Auth Password not present in ENV. Please set cxp_basicauth_password");
		}

		return cryptoAccess.unseal(cxpBasicAuthPassword);
	}
	
	@Value("${cxp.basicauth.password}")
	public void setCxpBasicAuthPassword(String cxpBasicAuthPassword) {
		this.cxpBasicAuthPassword = cryptoAccess.seal(cxpBasicAuthPassword);
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getSuccessTalkIndex() {
		return cryptoAccess.unseal(successTalkIndex);
	}
	
	@Value("${successTalk.elasticsearch.index}")
	public void setSuccessTalkIndex(String successTalkIndex) {
		this.successTalkIndex = cryptoAccess.seal(successTalkIndex);
	}

	public String getSuccessAcademyIndex() {
		return cryptoAccess.unseal(successAcademyIndex);
	}
	
	@Value("${successAcademy.elasticsearch.index}")
	public void setSuccessAcademyIndex(String successAcademyIndex) {
		this.successAcademyIndex = cryptoAccess.seal(successAcademyIndex);
	}
	
	public String getSuccessAcademyFilterIndex() {
		return cryptoAccess.unseal(successAcademyFilterIndex);
	}
	
	@Value("${successAcademy.elasticsearch.filter.index}")
	public void setSuccessAcademyFilterIndex(String successAcademyFilterIndex) {
		this.successAcademyFilterIndex = cryptoAccess.seal(successAcademyFilterIndex);
	}
	
	public long getSuccessTalkRegistrationSheetId() {
		return Long.parseLong(cryptoAccess.unseal(successTalkRegistrationSheetId));
	}
	
	@Value("${smartsheet.successTalk.registration.sheetId}")
	public void setSuccessTalkRegistrationSheetId(long successTalkRegistrationSheetId) {
		this.successTalkRegistrationSheetId = cryptoAccess.seal(String.valueOf(successTalkRegistrationSheetId));
	}

	public String getSmartsheetAccessToken() {
		return cryptoAccess.unseal(smartsheetAccessToken);
	}
	
	@Value("${smartsheet.accessToken}")
	public void setSmartsheetAccessToken(String smartsheetAccessToken) {
		this.smartsheetAccessToken = cryptoAccess.seal(smartsheetAccessToken);
	}

	public String getSuccessTalkUserRegistrationsIndex() {
		return cryptoAccess.unseal(successTalkUserRegistrationsIndex);
	}
	
	@Value("${successTalkUserRegistrations.elasticsearch.index}")
	public void setSuccessTalkUserRegistrationsIndex(String successTalkUserRegistrationsIndex) {
		this.successTalkUserRegistrationsIndex = cryptoAccess.seal(successTalkUserRegistrationsIndex);
	}

	public String getBookmarksIndex() {
		return cryptoAccess.unseal(bookmarksIndex);
	}
	
	@Value("${bookmarks.elasticsearch.index}")
	public void setBookmarksIndex(String bookmarksIndex) {
		this.bookmarksIndex = cryptoAccess.seal(bookmarksIndex);
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
		return cryptoAccess.unseal(awsRegion);
	}
	
	@Value("${cxpp.aws.region}")
	public void setAwsRegion(String awsRegion) {
		this.awsRegion = cryptoAccess.seal(awsRegion);
	}


	public String getBookmarkTableName() {
		return bookmarkTableName;
	}

	@Value("${cxpp.learning.bookmark.table}")
	public void setBookmarkTableName(String bookmarkTableName) {
		this.bookmarkTableName = bookmarkTableName;
	}

	public String getRbacExcludedEndPoints() {
		return cryptoAccess.unseal(rbacExcludedEndPoints);
	}

	@Value("${cxpp.rbac.exclude.path.patterns}")
	public void setRbacExcludedEndPoints(String rbacExcludedEndPoints) {
		this.rbacExcludedEndPoints = cryptoAccess.seal(rbacExcludedEndPoints);
	}

	public String getRbacIncludedEndPoints() {
		return cryptoAccess.unseal(rbacIncludedEndPoints);
	}
	
	@Value("${cxpp.rbac.include.path.patterns}")
	public void setRbacIncludedEndPoints(String rbacIncludedEndPoints) {
		this.rbacIncludedEndPoints = cryptoAccess.seal(rbacIncludedEndPoints);
	}

	public String getAuthUrl() {
		return cryptoAccess.unseal(authUrl);
	}
	
	@Value("${cxpp.user.management.auth.url}")
	public void setAuthUrl(String authUrl) {
		this.authUrl = cryptoAccess.seal(authUrl);
	}

	public boolean isNewLearningFeature() {
		return newLearningFeature;
	}

	public void setNewLearningFeature(boolean newLearningFeature) {
		this.newLearningFeature = newLearningFeature;
	}

	public String getUlPreferencesTableName() {
		return ulPreferencesTableName;
	}

	public void setUlPreferencesTableName(String ulPreferencesTableName) {
		this.ulPreferencesTableName = ulPreferencesTableName;
	}

	public String getPlsURL() {
		return plsURL;
	}

	public void setPlsURL(String plsURL) {
		this.plsURL = plsURL;
	}
}
