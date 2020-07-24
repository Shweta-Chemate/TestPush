package com.cisco.cx.training.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cisco.cx.training.app.filters.AuthFilter;
import com.cisco.cx.training.app.filters.RBACFilter;
import com.cisco.cx.training.constants.Constants;

@Configuration
public class FilterConfig {

    @Autowired
    private RBACFilter rbacFilter;
    
    @Autowired
    private AuthFilter authFilter;
    
    @Autowired
    private PropertyConfiguration config;
    @Bean
    public FilterRegistrationBean<RBACFilter> rbacFilter() {
        FilterRegistrationBean<RBACFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(rbacFilter);
        String[] includeUrlPatterns = config.getRbacIncludedEndPoints().trim().split(Constants.COMMA);
        registrationBean.addUrlPatterns(includeUrlPatterns);
        return registrationBean;
    }
    
    @Bean
    public FilterRegistrationBean<AuthFilter> loggingFilter() {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authFilter);
        registrationBean.addUrlPatterns("/v1/*");
        return registrationBean;
    }
}
