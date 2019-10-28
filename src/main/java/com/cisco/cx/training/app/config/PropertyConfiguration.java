package com.cisco.cx.training.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
		@PropertySource(value = "classpath:environment.properties"),
		@PropertySource(value = "file:/myapp/environment.properties", ignoreResourceNotFound = true)
})
public class PropertyConfiguration {
	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${elasticsearch.host}")
	private String elasticsearchHost;

	@Value("${elasticsearch.port}")
	private int elasticsearchPort;

	@Value("${elasticsearch.scheme}")
	private String elasticsearchScheme;

	@Value("${elasticsearch.username}")
	private String elasticsearchUsername;

	@Value("${elasticsearch.password}")
	private String elasticsearchPassword;

	@Value("${cisco.ldap.external.url}")
	private String ldapUrl;

	@Value("${cisco.ldap.userid}")
	private String ldapUserId;

	@Value("${cisco.oauth.token.url}")
	private String ciscoOauthTokenUrl;

	@Value("${entitlement.user.party.affiliation.url}")
	private String entitlementUserPartyAffiliationUrl;

	@Value("${cxp.emailapi.context.url}")
	private String cxpEmailApiContextUrl;

	@Value("${cxp.emailapi.readiness.url}")
	private String cxpEmailReadinessUrl;
	
    @Value("${successTalk.elasticsearch.index}")
    private String successTalkIndex;
    
    @Value("${smartsheet.successTalk.registration.sheetId}")
    private long successTalkRegistrationSheetId;
    
    @Value("${smartsheet.accessToken}")
    private String smartsheetAccessToken;
    
    @Value("${successTalkUserRegistrations.elasticsearch.index}")
    private String successTalkUserRegistrationsIndex;

    @Value("${bookmarks.elasticsearch.index}")
    private String bookmarksIndex;

	public String getApplicationName() { return applicationName; }

	public String getElasticsearchHost() { return elasticsearchHost; }

	public int getElasticsearchPort() { return elasticsearchPort; }

	public String getElasticsearchScheme() { return elasticsearchScheme; }

	public String getElasticsearchUsername() { return elasticsearchUsername; }

	public String getElasticsearchPassword() { return elasticsearchPassword; }

	public String getLdapUrl() { return ldapUrl; }

	public String getLdapUserId() { return ldapUserId; }

	public String getCiscoOauthTokenUrl() { return ciscoOauthTokenUrl; }

	public String getEntitlementUserPartyAffiliationUrl() { return entitlementUserPartyAffiliationUrl; }

	public String getCxpEmailApiContextUrl() { return cxpEmailApiContextUrl; }

	public String getCxpEmailReadinessUrl() { return cxpEmailReadinessUrl; }
	
    public String getSuccessTalkIndex() {
        return successTalkIndex;
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
}
