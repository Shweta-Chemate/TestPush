package com.cisco.cx.training.test;

import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.filters.XSSFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class XSSFilterTest {

  @Test
  void doFilter() throws Exception {
    XSSFilter filter = new XSSFilter();
    MockHttpServletResponse servletResponse = new MockHttpServletResponse();
    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.addHeader("partnerId", "293934");
    servletRequest.addParameter("partnerId", "293934");
    MockFilterChain filterChain = new MockFilterChain();
    Assertions.assertDoesNotThrow(
        () -> filter.doFilterInternal(servletRequest, servletResponse, filterChain));
  }

  @Test
  void doFilterInvalidInput() throws Exception {
    XSSFilter filter = new XSSFilter();
    MockHttpServletResponse servletResponse = new MockHttpServletResponse();

    final MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.addHeader("partnerId", "293934<script>");
    servletRequest.addParameter("partnerId", "293934");
    MockFilterChain filterChain = new MockFilterChain();
    Assertions.assertThrows(
        BadRequestException.class,
        () -> filter.doFilterInternal(servletRequest, servletResponse, filterChain));

    final MockHttpServletRequest servletRequest2 = new MockHttpServletRequest();
    servletRequest2.addHeader("partnerId", "293934");
    servletRequest2.addParameter("partnerId", "293934<script>");
    Assertions.assertDoesNotThrow(
        () -> filter.doFilter(servletRequest2, servletResponse, filterChain));
  }
}
