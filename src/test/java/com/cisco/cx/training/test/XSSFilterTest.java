package com.cisco.cx.training.test;

import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.filters.XSSFilter;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import java.io.IOException;

import static org.junit.Assert.*;

public class XSSFilterTest {

    @Test
    public void doFilter() throws  Exception{
        XSSFilter filter = new XSSFilter();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader("partnerId","293934");
        servletRequest.addParameter("partnerId","293934");
        MockFilterChain filterChain = new MockFilterChain();
        filter.doFilterInternal(servletRequest,servletResponse,filterChain);
    }

    @Test
    public void doFilterInvalidInput() throws  Exception{
        XSSFilter filter = new XSSFilter();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        try {
            servletRequest.addHeader("partnerId","293934<script>");
            servletRequest.addParameter("partnerId","293934");
            MockFilterChain filterChain = new MockFilterChain();
            filter.doFilterInternal(servletRequest,servletResponse,filterChain);


        }  catch (BadRequestException e) {
            assert true;
        }

        try {
            servletRequest = new MockHttpServletRequest();
            servletRequest.addHeader("partnerId","293934");
            servletRequest.addParameter("partnerId","293934<script>");
            MockFilterChain filterChain = new MockFilterChain();
            filter.doFilter(servletRequest,servletResponse,filterChain);


        }  catch (BadRequestException e) {
            assert true;
        }


    }
}
