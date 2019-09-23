package com.cisco.services.app.service;

import com.cisco.services.app.config.PropertyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.services.models.UserProfile;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.HashMap;
import java.util.Hashtable;

@Service
public class CiscoProfileService {
    private static final Logger LOG = LoggerFactory.getLogger(CiscoProfileService.class);

    public static final String LDAP_INITIAL_CONTEXT = "com.sun.jndi.ldap.LdapCtxFactory";
    public static final String CCO_BASE = "ou=ccoentities,o=cco.cisco.com";

    @Autowired
    private PropertyConfiguration config;

    /**
     * getUserProfile - queries LDAP and returns the CCO profile for given user.
     * @param userid - the cco id of the user to search in LDAP
     * @return
     */
    public UserProfile getUserProfile(String userid) {
        UserProfile myUser = null;
        LOG.info("Fetching Cisco Profile data for " + userid);
        HashMap<String, String> myUserData = this.getLDAPInfo(userid);

        // Setting the User attributes to the UserBean
        if (myUserData != null && !myUserData.isEmpty()) {
            myUser = new UserProfile();
            myUser.setUserId((String) myUserData.get("uid"));
            LOG.debug("UID = " + (String) myUserData.get("uid"));

            myUser.setFirstName((String) myUserData.get("givenName"));
            LOG.debug("givenName = " + (String) myUserData.get("givenName"));

            myUser.setLastName((String) myUserData.get("sn"));
            LOG.debug("sn = " + (String) myUserData.get("sn"));

            myUser.setCiscoUid((String) myUserData.get("ciscoUid"));
            LOG.debug("ciscoUid = " + (String) myUserData.get("ciscoUid"));

            myUser.setMailId((String) myUserData.get("mail"));
            LOG.debug("mail = " + (String) myUserData.get("mail"));

            myUser.setCountry((String) myUserData.get("co"));
            LOG.debug("country = " + (String) myUserData.get("co"));

            String accessLevel = (String) myUserData.get("accessLevel");
            myUser.setAccessLevel(accessLevel);
            LOG.debug("accessLevel = " + accessLevel);

            String userType = resolveUserType(accessLevel);
            myUser.setUserType(userType);
        }

        return myUser;
    }

    public static String resolveUserType(String accessLevel) {
        String response = null;

        switch (accessLevel) {
            case "0":
                response = "Guest";
                break;
            case "1":
                response = "Registered User";
                break;
            case "2":
                response = "Customer";
                break;
            case "3":
                response = "Partner";
                break;
            case "4":
                response = "Employee";
                break;
            default:
                response = "Guest";
                break;
        }

        return response;
    }

    // Fetching the User data from the LDAP server
    public HashMap<String, String> getLDAPInfo(String userId) {
        HashMap<String, String> record = null;
        long ldapStartTime = System.currentTimeMillis();

        try {
            // Retrieve a JNDI Context
            DirContext ctx = null;

            Hashtable<String, String> env = new Hashtable<String, String>(); // ldap api needs hashtable, lame
            env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_INITIAL_CONTEXT);
            env.put(Context.PROVIDER_URL, config.getLdapUrl());
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid=" + config.getLdapUserId() + ",OU=Generics,O=cco.cisco.com");
            env.put(Context.SECURITY_CREDENTIALS, config.getLdapPassword());

            try {
                ctx = new InitialDirContext(env);
            } catch (NamingException e) {
                LOG.error("CiscoProfileService.getLDAPInfo - Problem connecting to LDAP server: " + e, e);
            }

            // Query
            NamingEnumeration<SearchResult> entries = null;

            if (ctx != null) {
                String searchFilter = "(uid=" + userId + ")";
                SearchControls ctls = new SearchControls();
                ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                try {
                    entries = ctx.search(CCO_BASE, searchFilter, ctls);
                } catch (NamingException e) {
                    LOG.error("CiscoProfileService.getLDAPInfo - Problem in LDAP search: " + e, e);
                }

                try {
                    ctx.close();
                } catch (NamingException e) {
                    LOG.error("CiscoProfileService.getLDAPInfo - Disconnect failed: " + e, e);
                }
            }

            try {
                if (entries != null && entries.hasMore()) {
                    record = new HashMap<String, String>();
                    do {
                        SearchResult entry = (SearchResult) entries.next();
                        Attributes srchAttrs = entry.getAttributes();
                        NamingEnumeration<String> ne = srchAttrs.getIDs();

                        while (ne.hasMore()) {
                            String neStr = (String) ne.next();
                            Attribute at = srchAttrs.get(neStr);
                            record.put(neStr, at.get().toString());
                        }
                    } while (entries.hasMore());
                }
            } catch (NamingException nex) {
                LOG.error("CiscoProfileService.getLDAPInfo - Error searching LDAP - " + nex, nex);
            }
        } finally {
            LOG.info("PERF_TIME_TAKEN LDAP_QUERY | " + userId + " | " + (System.currentTimeMillis() - ldapStartTime));
        }

        return record;
    }
}
