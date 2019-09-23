package com.cisco.services.app.service;

import com.cisco.services.app.config.PropertyConfiguration;
import com.cisco.services.app.exception.GenericException;
import com.cisco.services.constants.Constants;
import com.cisco.services.models.EmailContent;
import com.cisco.services.models.EmailRequest;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;

@Service
public class EmailService {
    private final static Logger LOG = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private HttpService httpService;

    @Autowired
    private PropertyConfiguration config;

    public boolean isEmailServerRunning() {
        HashMap<String, Object> emailReadyResponse = this.httpService.makeHttpGetCall(config.getCxpEmailReadinessUrl(), new HashMap<>(), null, HashMap.class);
        Object mailHostStatus = emailReadyResponse.get("mail.host");

        return (mailHostStatus != null && StringUtils.equalsIgnoreCase(mailHostStatus.toString(), "ok"));
    }

    /**
     * sendEmail - sends an email using the email fields sent in the request
     *
     * @param email - the Email Request object
     * @param headers - Headers to be passed to the Email api.  Setting the X-Mashery-Handshake header value is MANDATORY.
     * @return - method will return "Email Sent" string if email is sent successfully
     * @throws RestClientException thrown if there is an HTTP error while calling the Email API
     */
    public String sendEmail (EmailRequest email, MultiValueMap<String, String> headers) throws RestClientException {
        long sendStartTime = System.currentTimeMillis();
        this.verifyMasheryHeaderAvailable(headers);

        try {
            return this._sendEmail("/v1/send", email, headers);
        } finally {
            LOG.info("PERF_TIME_TAKEN EMAIL_API_SEND | " + email.getTo() + " | " + (System.currentTimeMillis() - sendStartTime));
        }
    }

    /**
     * sendEmail - send an email using a template saved previously
     *
     * @param email - the Email Request object
     * @param headers - Headers to be passed to the Email api.  Setting the X-Mashery-Handshake header value is MANDATORY.
     * @param templateName - the template name to use to send email.
     * @return - method will return "Email Sent" string if email is sent successfully
     * @throws RestClientException thrown if there is an HTTP error while calling the Email API
     */
    public String sendEmail (EmailRequest email, MultiValueMap<String, String> headers, String templateName) throws RestClientException {
        long sendStartTime = System.currentTimeMillis();
        this.verifyMasheryHeaderAvailable(headers);

        try {
            return this._sendEmail("/v1/send/" + templateName, email, headers);
        } finally {
            LOG.info("PERF_TIME_TAKEN EMAIL_API_TEMPLATE_SEND | " + email.getTo() + " | " + (System.currentTimeMillis() - sendStartTime));
        }
    }

    /**
     * saveTemplate - save an email template
     *
     * @param templateName - the name of template to save. note: if a template with this name exists, it will be overwritten.
     * @param templateContent - the template content to save
     * @param headers - Headers to be passed to the Email api.  Setting the X-Mashery-Handshake header value is MANDATORY.
     * @return - method will return "Template Saved" string if template is saved successfully
     * @throws RestClientException thrown if there is an HTTP error while calling the Email API
     */
    public String saveTemplate (String templateName, EmailContent templateContent, MultiValueMap<String, String> headers) throws RestClientException {
        long saveStartTime = System.currentTimeMillis();
        this.verifyMasheryHeaderAvailable(headers);

        try {
            return this.httpService.makeHttpPostCallWithBody(config.getCxpEmailApiContextUrl() + "/v1/template/" + templateName, templateContent, new HttpHeaders(headers), String.class);
        } finally {
            LOG.info("PERF_TIME_TAKEN EMAIL_API_TEMPLATE_SAVE | " + templateName + " | " + (System.currentTimeMillis() - saveStartTime));
        }
    }

    // ---- helper methods ----

    private String _sendEmail (String emailApiPath, EmailRequest email, MultiValueMap<String, String> headers) throws RestClientException {
        // set the email source if not set in request
        if (StringUtils.isBlank(email.getSource())) {
            String appSource = config.getApplicationName();
            appSource = StringUtils.isBlank(appSource) ? System.getenv("HOSTNAME") : appSource;
            email.setSource(appSource);
        }

        return this.httpService.makeHttpPostCallWithBody(config.getCxpEmailApiContextUrl() + emailApiPath, email, new HttpHeaders(headers), String.class);
    }

    private void verifyMasheryHeaderAvailable(MultiValueMap<String, String> headers) {
        if (headers == null || !headers.containsKey(Constants.MASHERY_HANDSHAKE_HEADER_NAME)) {
            LOG.error("Note to Developer: Please set " + Constants.MASHERY_HANDSHAKE_HEADER_NAME + " in headers.");
            throw new GenericException(Constants.MASHERY_HANDSHAKE_HEADER_NAME + " header is missing in the call to EmailService.");
        }
    }
}
