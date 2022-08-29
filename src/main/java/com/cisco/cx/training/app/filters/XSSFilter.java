package com.cisco.cx.training.app.filters;

import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.util.XSSUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class XSSFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(XSSFilter.class);

  @Override
  public void doFilterInternal(
      HttpServletRequest httpRequest, HttpServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    LOGGER.info("Inside XSSFilter====================================>");
    final Enumeration<String> headerNames = httpRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();

      String userInput = httpRequest.getHeader(headerName);

      if (userInput != null && !userInput.equalsIgnoreCase(XSSUtil.checkXSS("header", userInput))) {
        LOGGER.info(headerName + "--" + userInput);
        throw new BadRequestException("Bad input in header: " + userInput);
      }
    }

    final Enumeration<String> parameterNames = httpRequest.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String paramName = parameterNames.nextElement();
      String userInput = httpRequest.getParameter(paramName);

      if (userInput != null
          && !userInput.equalsIgnoreCase(XSSUtil.checkXSS(paramName, userInput))) {
        LOGGER.info(paramName + "---" + userInput);
        throw new BadRequestException("Bad input in parameters : " + userInput);
      }
    }
    filterChain.doFilter(httpRequest, servletResponse);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return (!path.contains("/v1/"));
  }

  public String convertObjectToJson(Object object) throws JsonProcessingException {
    if (object == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }
}
