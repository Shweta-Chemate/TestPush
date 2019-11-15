package com.cisco.cx.training.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cisco.cx.training.constants.Constants;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Value("${cxpp.interceptor.exclude.path.patterns}")
    public String excludePathPatterns ;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	 String[] excludePatterns = excludePathPatterns.split(Constants.COMMA.trim());
        registry.addInterceptor(new RequestInterceptor()).excludePathPatterns(excludePatterns);
    }
}
