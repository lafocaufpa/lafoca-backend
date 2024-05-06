package com.ufpa.lafocabackend.core.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface CheckSecurityPermissionMethods {


    @PreAuthorize("hasAnyAuthority(@lafocaSecurity.ADMIN_LEVEL_1)")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Level1 {}

    @PreAuthorize("hasAnyAuthority(@lafocaSecurity.ADMIN_LEVEL_1, @lafocaSecurity.ADMIN_LEVEL_2) ")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Level1OrLevel2 {}

    @interface User {

        @PreAuthorize("hasAnyAuthority(@lafocaSecurity.ADMIN_LEVEL_1) ")
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface Level1 {

        }

        @PreAuthorize("@lafocaSecurity.userHimself(#userId) or hasAnyAuthority(@lafocaSecurity.ADMIN_LEVEL_1, @lafocaSecurity.ADMIN_LEVEL_2)")
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface UserHimselfOrLevel1OrLevel2 {

        }

        @PreAuthorize("@lafocaSecurity.userHimself(#userId) or hasAnyAuthority(@lafocaSecurity.ADMIN_LEVEL_1)")
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface UserHimselfOrLevel1 {

        }
    }
}
