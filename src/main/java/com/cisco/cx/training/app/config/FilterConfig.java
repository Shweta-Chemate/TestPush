package com.cisco.cx.training.app.config;

import com.cisco.cx.training.app.filters.AuthFilter;
import com.cisco.cx.training.app.filters.LogFilter;
import com.cisco.cx.training.app.filters.RBACFilter;
import com.cisco.cx.training.constants.Constants;
import com.cisco.services.common.restclient.DefaultHeadersSupplier;
import com.cisco.services.common.restclient.RestClient;
import com.cisco.services.common.restclient.RestTemplateClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FilterConfig {

  @Autowired private PropertyConfiguration config;

  @Bean
  public FilterRegistrationBean<LogFilter> logPatternFilter(LogFilter logFilter) {
    FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(logFilter);
    String[] includeUrlPatterns = config.getRbacIncludedEndPoints().trim().split(Constants.COMMA);
    registrationBean.addUrlPatterns(includeUrlPatterns);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<RBACFilter> rbacFilter(RBACFilter rbacFilter) {
    FilterRegistrationBean<RBACFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(rbacFilter);
    String[] includeUrlPatterns = config.getRbacIncludedEndPoints().trim().split(Constants.COMMA);
    registrationBean.addUrlPatterns(includeUrlPatterns);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<AuthFilter> loggingFilter(AuthFilter authFilter) {
    FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(authFilter);
    registrationBean.addUrlPatterns("/v1/*");
    return registrationBean;
  }

  @Bean
  public RestClient restClient(
      RestTemplate restTemplate, DefaultHeadersSupplier defaultHeadersSupplier) {
    return new RestTemplateClient(restTemplate, defaultHeadersSupplier);
  }
}
