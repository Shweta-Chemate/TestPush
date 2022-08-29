package com.cisco.cx.training.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.exception.BadRequestException;
import com.cisco.cx.training.app.filters.RBACFilter;
import com.cisco.cx.training.util.AuthorizationUtil;
import com.cisco.services.common.restclient.RestClient;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestRBACFilter {

  @InjectMocks private RBACFilter rbacFilter = new RBACFilter();

  @Mock private FilterConfig filterConfig;

  @Mock private PropertyConfiguration propertyConfig;

  @Mock private HttpServletRequest req;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain chain;

  @Mock private AuthorizationUtil authUtil;

  @Mock private RestClient restClient;

  @Autowired private ResourceLoader resourceLoader;

  private String XMasheryHeader;

  private String authResult;

  private String puid = "101";
  public static final String MASHERY_HANDSHAKE_HEADER_NAME = "X-Mashery-Handshake";
  public static final String PUID = "puid";

  @BeforeEach
  public void init() throws IOException {
    this.XMasheryHeader =
        new String(Base64.encodeBase64(loadFromFile("mock/auth-mashery-user1.json").getBytes()));
    this.authResult = new String(loadFromFile("mock/authResult.json").getBytes());
  }

  private String loadFromFile(String filePath) throws IOException {
    return new String(
        Files.readAllBytes(resourceLoader.getResource("classpath:" + filePath).getFile().toPath()));
  }

  @Test
  void testInit() throws ServletException {
    Assertions.assertDoesNotThrow(() -> rbacFilter.init(filterConfig));
  }

  @Test
  void testDestroy() {
    Assertions.assertDoesNotThrow(() -> rbacFilter.destroy());
  }

  @Test
  void testFilterNoRBACRequired() throws IOException, ServletException {
    Assertions.assertDoesNotThrow(() -> rbacFilter.doFilter(req, response, chain));
  }

  @Test
  void testFilterMissingMashery() throws IOException, ServletException {
    when(req.getRequestURI()).thenReturn("/testpath");
    when(propertyConfig.getRbacExcludedEndPoints()).thenReturn("/excludedpath");
    assertThrows(
        BadRequestException.class,
        () -> {
          rbacFilter.doFilter(req, response, chain);
        });
  }

  @Test
  void testFilterMissingPuid() throws IOException, ServletException {
    when(req.getRequestURI()).thenReturn("/testpath");
    when(propertyConfig.getRbacExcludedEndPoints()).thenReturn("/excludedpath");
    when(req.getHeader(MASHERY_HANDSHAKE_HEADER_NAME)).thenReturn(this.XMasheryHeader);
    assertThrows(
        BadRequestException.class,
        () -> {
          rbacFilter.doFilter(req, response, chain);
        });
  }

  @Test
  void testFilterAuthResultNull() throws IOException, ServletException {
    when(req.getRequestURI()).thenReturn("/testpath");
    when(propertyConfig.getRbacExcludedEndPoints()).thenReturn("/excludedpath");
    when(req.getHeader(MASHERY_HANDSHAKE_HEADER_NAME)).thenReturn(this.XMasheryHeader);
    when(req.getHeader(PUID)).thenReturn(this.puid);
    when(AuthorizationUtil.invokeAuthAPI(
            "sntccbr5@hotmail.com", this.puid, this.XMasheryHeader, propertyConfig, restClient))
        .thenReturn(this.authResult);
    assertThrows(
        Exception.class,
        () -> {
          rbacFilter.doFilter(req, response, chain);
        });
  }
}
