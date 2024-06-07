package com.arrow.Arrow.config;

import com.arrow.Arrow.dto.Role;
import com.arrow.Arrow.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class Authorities {
    User user=new User();
    Collection<Role> roles = Collections.singleton(user.getRole());
    Collection<GrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .collect(Collectors.toList());
}
