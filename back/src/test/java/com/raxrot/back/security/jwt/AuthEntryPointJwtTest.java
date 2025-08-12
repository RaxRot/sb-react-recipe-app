package com.raxrot.back.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthEntryPointJwtTest {

    @Test
    void commence_shouldWrite401Json() throws IOException, ServletException {
        AuthEntryPointJwt entryPoint = new AuthEntryPointJwt();

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/api/secure");
        MockHttpServletResponse res = new MockHttpServletResponse();
        AuthenticationException ex = new AuthenticationException("Bad credentials") {};

        entryPoint.commence(req, res, ex);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(res.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        Map<?, ?> body = new ObjectMapper().readValue(res.getContentAsByteArray(), Map.class);
        assertThat(body.get("status")).isEqualTo(401);
        assertThat(body.get("error")).isEqualTo("Unauthorized");
        assertThat(body.get("message")).isEqualTo("Bad credentials");
        assertThat(body.get("path")).isEqualTo("/api/secure");
    }
}