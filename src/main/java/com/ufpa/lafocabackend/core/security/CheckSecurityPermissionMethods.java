package com.ufpa.lafocabackend.core.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface CheckSecurityPermissionMethods {


    @PreAuthorize("hasAnyAuthority('ADMIN_LEVEL_1') ")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface L1 {}

    @interface User {

        @PreAuthorize("hasAnyAuthority('ADMIN_LEVEL_1', 'ADMIN_LEVEL_2') ")
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface L1L2 {

        }

        @PreAuthorize("@lafocaSecurity.userHimself(#userId) or hasAnyAuthority('ADMIN_LEVEL_1', 'ADMIN_LEVEL_2')")
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface L1L2OrUserHimself {

        }

        @PreAuthorize("@lafocaSecurity.userHimself(#userId) or hasAnyAuthority('ADMIN_LEVEL_1')")
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface L1OrUserHimself {

        }
    }
}
