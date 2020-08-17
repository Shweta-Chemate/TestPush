package com.cisco.cx.training.test;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.filters.RBACFilter;
import com.cisco.cx.training.util.AuthorizationUtil;

@RunWith(SpringRunner.class)
public class TestRBACFilter {
	
	@InjectMocks
	private RBACFilter rbacFilter = new RBACFilter();
	
	@Mock
	private FilterConfig filterConfig;
	
	@Mock
	private PropertyConfiguration propertyConfig;
	
	@Mock
	private HttpServletRequest req;
	
	@Mock
	private HttpServletResponse response;
	
	@Mock
	private FilterChain chain;
	
	@Mock
	private AuthorizationUtil authUtil;
	
	@Mock
	private RestTemplate restTemplate;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private String XMasheryHeader;
	
	private String authResult;
	
	private String puid = "101";
	public static final String MASHERY_HANDSHAKE_HEADER_NAME = "X-Mashery-Handshake";
	public static final String PUID="puid";
	
	
	@Before
	public void init() throws IOException {
		this.XMasheryHeader = new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
		this.authResult = new String(loadFromFile("mock/authResult.json").getBytes());
	}
	
	private String loadFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
	}
	
	@Test
	public void testInit() throws ServletException
	{
		rbacFilter.init(filterConfig);
	}

	@Test
	public void testDestroy()
	{
		rbacFilter.destroy();
	}
	
	@Test
	public void testFilterNoRBACRequired() throws IOException, ServletException
	{
		rbacFilter.doFilter(req, response, chain);
		
	}
	
	@Test
	(expected = BadRequestException.class)
	public void testFilterMissingMashery() throws IOException, ServletException
	{
		when(req.getRequestURI()).thenReturn("/testpath");
		when(propertyConfig.getRbacExcludedEndPoints()).thenReturn("/excludedpath");
		rbacFilter.doFilter(req, response, chain);
	}
	
	@Test
	(expected = BadRequestException.class)
	public void testFilterMissingPuid() throws IOException, ServletException
	{
		when(req.getRequestURI()).thenReturn("/testpath");
		when(propertyConfig.getRbacExcludedEndPoints()).thenReturn("/excludedpath");
		when(req.getHeader(MASHERY_HANDSHAKE_HEADER_NAME)).thenReturn(this.XMasheryHeader);
		rbacFilter.doFilter(req, response, chain);
	}
	
	@Test
	(expected = Exception.class)
	public void testFilterAuthResultNull() throws IOException, ServletException
	{
		when(req.getRequestURI()).thenReturn("/testpath");
		when(propertyConfig.getRbacExcludedEndPoints()).thenReturn("/excludedpath");
		when(req.getHeader(MASHERY_HANDSHAKE_HEADER_NAME)).thenReturn(this.XMasheryHeader);
		when(req.getHeader(PUID)).thenReturn(this.puid);
		when(AuthorizationUtil.invokeAuthAPI("sntccbr5@hotmail.com", this.puid, this.XMasheryHeader, propertyConfig, restTemplate)).thenReturn(this.authResult);
		rbacFilter.doFilter(req, response, chain);
	}
}