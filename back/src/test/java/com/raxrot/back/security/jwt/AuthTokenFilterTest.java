package com.raxrot.back.security.jwt;

import com.raxrot.back.security.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldAuthenticate_whenTokenValid() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        UserDetailsServiceImpl uds = mock(UserDetailsServiceImpl.class);
        FilterChain chain = mock(FilterChain.class);

        AuthTokenFilter filter = new AuthTokenFilter();

        org.springframework.test.util.ReflectionTestUtils.setField(filter, "jwtUtils", jwtUtils);
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "userDetailsService", uds);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/api/recipes");
        req.addHeader("Authorization", "Bearer good.jwt");
        MockHttpServletResponse res = new MockHttpServletResponse();

        when(jwtUtils.getJwtFromHeader(req)).thenReturn("good.jwt");
        when(jwtUtils.validateJwtToken("good.jwt")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("good.jwt")).thenReturn("alice");

        var user = User.withUsername("alice").password("x").authorities("ROLE_USER").build();
        when(uds.loadUserByUsername("alice")).thenReturn(user);

        filter.doFilterInternal(req, res, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(auth.getName()).isEqualTo("alice");
        assertThat(auth.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
        verify(chain).doFilter(req, res);
    }

    @Test
    void doFilterInternal_shouldSkip_whenTokenInvalid() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        UserDetailsServiceImpl uds = mock(UserDetailsServiceImpl.class);
        FilterChain chain = mock(FilterChain.class);

        AuthTokenFilter filter = new AuthTokenFilter();
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "jwtUtils", jwtUtils);
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "userDetailsService", uds);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer bad.jwt");
        MockHttpServletResponse res = new MockHttpServletResponse();

        when(jwtUtils.getJwtFromHeader(req)).thenReturn("bad.jwt");
        when(jwtUtils.validateJwtToken("bad.jwt")).thenReturn(false);

        filter.doFilterInternal(req, res, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(uds, never()).loadUserByUsername(ArgumentMatchers.anyString());
        verify(chain).doFilter(req, res);
    }

    @Test
    void doFilterInternal_shouldSkip_whenNoAuthorizationHeader() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        UserDetailsServiceImpl uds = mock(UserDetailsServiceImpl.class);
        FilterChain chain = mock(FilterChain.class);

        AuthTokenFilter filter = new AuthTokenFilter();
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "jwtUtils", jwtUtils);
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "userDetailsService", uds);

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        when(jwtUtils.getJwtFromHeader(req)).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(req, res);
    }
}