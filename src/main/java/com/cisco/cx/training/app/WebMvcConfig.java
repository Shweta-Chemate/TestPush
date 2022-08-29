package com.cisco.cx.training.app;

import com.cisco.cx.training.constants.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  @Value("${cxpp.interceptor.exclude.path.patterns}")
  public String excludePathPatterns;

  @Value("${cxpp.interceptor.include.path.patterns}")
  public String includePathPatterns;

  @Bean
  public RequestInterceptor requestInterceptor() {
    return new RequestInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    String[] excludePatterns = excludePathPatterns.split(Constants.COMMA.trim());
    String[] includePatterns = includePathPatterns.split(Constants.COMMA.trim());
    registry
        .addInterceptor(requestInterceptor())
        .addPathPatterns(includePatterns)
        .excludePathPatterns(excludePatterns);
  }
}
