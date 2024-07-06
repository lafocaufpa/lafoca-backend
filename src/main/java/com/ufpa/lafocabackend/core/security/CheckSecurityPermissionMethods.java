package com.ufpa.lafocabackend.core.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface CheckSecurityPermissionMethods {

    @PreAuthorize("@lafocaSecurity.isUserManager() or @lafocaSecurity.isAdmin()")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AdminOrManagerUsersGroups {
    }

    @PreAuthorize("@lafocaSecurity.isEditor() or @lafocaSecurity.isAdmin()")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AdminOrEditor {
    }

    @PreAuthorize("@lafocaSecurity.isEditor() or @lafocaSecurity.isModerator() or @lafocaSecurity.isAdmin()")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AdminOrEditorOrModerator {
    }

    @PreAuthorize("@lafocaSecurity.userHimself(#userId, #userEmail) or @lafocaSecurity.isUserManager() or @lafocaSecurity.isAdmin()")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface UserHimselfOrManagerUsersGroupsOrAdmin {

    }

}
