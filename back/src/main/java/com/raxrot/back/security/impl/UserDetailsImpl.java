package com.raxrot.back.security.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raxrot.back.models.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String email;
    private String username;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().name());
        return new UserDetailsImpl(user.getId(),
                user.getEmail(), user.getUsername(), user.getPassword(),List.of(grantedAuthority));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
