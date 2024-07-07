package com.ufpa.lafocabackend.core.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface CheckSecurityPermissionMethods {

    @PreAuthorize("@lafocaSecurity.isAdminOrUserGroupManager()")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AdminOrManagerUsersGroups {
    }

    @PreAuthorize("@lafocaSecurity.isAdminOrEditor()")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AdminOrEditor {
    }

    @PreAuthorize("@lafocaSecurity.isAdminOrEditorOrModerator()")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AdminOrEditorOrModerator {
    }

    @PreAuthorize("@lafocaSecurity.checkUserHimselfOrManagerOrAdmin(#userId, #userEmail)")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface UserHimselfOrManagerUsersGroupsOrAdmin {

    }

}
