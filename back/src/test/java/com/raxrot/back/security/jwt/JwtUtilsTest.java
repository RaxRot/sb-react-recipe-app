package com.raxrot.back.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private JwtUtils jwt;

    private static final String BASE64_SECRET =
            "dGhpc2lzbG9uZ2Jhc2U2NGtleWZvcmp3dC1oczI1Ni10ZXN0LXNlY3JldA==";

    @BeforeEach
    void setUp() {
        jwt = new JwtUtils();
        ReflectionTestUtils.setField(jwt, "jwtSecret", BASE64_SECRET);
        ReflectionTestUtils.setField(jwt, "jwtExpirationMs", 60_000); // 60s
    }

    @Test
    void getJwtFromHeader_shouldExtractBearerToken() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer aaa.bbb.ccc");
        assertThat(jwt.getJwtFromHeader(req)).isEqualTo("aaa.bbb.ccc");
    }

    @Test
    void getJwtFromHeader_shouldReturnNull_whenMissingOrInvalid() {
        assertThat(jwt.getJwtFromHeader(new MockHttpServletRequest())).isNull();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Token x");
        assertThat(jwt.getJwtFromHeader(req)).isNull();
    }

    @Test
    void generate_validate_parse_roundTrip() {
        UserDetails ud = User.withUsername("alice").password("x").authorities("ROLE_USER").build();
        String token = jwt.generateTokenFromUsername(ud);

        assertThat(jwt.validateJwtToken(token)).isTrue();
        assertThat(jwt.getUserNameFromJwtToken(token)).isEqualTo("alice");
    }

    @Test
    void validate_shouldReturnFalse_onMalformed() {
        assertThat(jwt.validateJwtToken("not.a.jwt")).isFalse();
    }

    @Test
    void validate_shouldReturnFalse_onExpired() throws Exception {
        ReflectionTestUtils.setField(jwt, "jwtExpirationMs", 1); // 1 ms
        UserDetails ud = User.withUsername("bob").password("x").authorities("USER").build();
        String token = jwt.generateTokenFromUsername(ud);

        Thread.sleep(5);
        assertThat(jwt.validateJwtToken(token)).isFalse();
    }
}