package com.arrow.Arrow.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public enum Role {
    INDI_USER,
    ADMIN,
    AGENT;

    public static Collection<Role> getAllRoles(String username) {
        return Collections.unmodifiableCollection(Arrays.asList(Role.values()));
    }

}
