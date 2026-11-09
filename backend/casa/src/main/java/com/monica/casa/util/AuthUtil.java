package com.monica.casa.util;

import org.springframework.security.core.Authentication;

public final class AuthUtil {

    private AuthUtil() {
    }

    public static boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
