package com.ufpa.lafocabackend.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class LafocaSecurity {

    private static final String VIEW_CONTENT = "VIEW_CONTENT";
    private static final String EDIT_CONTENT = "EDIT_CONTENT";
    private static final String DELETE_CONTENT = "DELETE_CONTENT";
    private static final String MANAGE_USERS = "MANAGE_USERS";
    private static final String MANAGE_GROUPS = "MANAGE_GROUPS";
    private static final String FULL_ACCESS = "FULL_ACCESS";

    private Authentication getAuthentication()  {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getUserId(){

        final Object userAutheticated = getAuthentication().getPrincipal();
        if(userAutheticated.equals("anonymousUser"))
            return "anonymousUser";

        Jwt jwt = (Jwt) userAutheticated;

        return jwt.getClaimAsString("user_id");
    }

    private String getEmail(){

        final Object userAutheticated = getAuthentication().getPrincipal();
        if(userAutheticated.equals("anonymousUser"))
            return "anonymousUser";

        Jwt jwt = (Jwt) userAutheticated;

        return jwt.getClaimAsString("sub");
    }

    private boolean userHimself(String userId, String userEmail){

        boolean equals = getUserId().equals(userId);
        if(!equals) {
            equals = userHimselfWithEmail(userEmail);
        }
        return equals;
    }

    private boolean userHimselfWithEmail(String email){
        return getEmail().equals(email);
    }

    private boolean isAdmin() {
        Collection<? extends GrantedAuthority> authorities = getAuthentication().getAuthorities();
        if (authorities != null) {
            return authorities.stream()
                    .anyMatch(authority -> authority.getAuthority().equals(FULL_ACCESS));
        }
        return false;
    }

    private boolean isEditor() {
        Collection<? extends GrantedAuthority> authorities = getAuthentication().getAuthorities();
        if (authorities != null) {
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet())
                    .containsAll(Arrays.asList(VIEW_CONTENT, EDIT_CONTENT, DELETE_CONTENT));
        }
        return false;
    }

    private boolean isModerator() {
        Collection<? extends GrantedAuthority> authorities = getAuthentication().getAuthorities();
        if (authorities != null) {
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet())
                    .containsAll(Arrays.asList(VIEW_CONTENT, EDIT_CONTENT));
        }
        return false;
    }

    private boolean isUserGroupManager() {
        Collection<? extends GrantedAuthority> authorities = getAuthentication().getAuthorities();
        if (authorities != null) {
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet())
                    .containsAll(Arrays.asList(MANAGE_GROUPS, MANAGE_USERS));
        }
        return false;
    }

    public boolean checkUserHimselfOrManagerOrAdmin(String userId, String userEmail) {
        return userHimself(userId, userEmail) || isUserGroupManager() || isAdmin();
    }

    public boolean isAdminOrEditorOrModerator() {
        return isAdmin() || isEditor() || isModerator();
    }

    public boolean isAdminOrEditor() {
        return isAdmin() || isEditor();
    }
    public boolean isAdminOrUserGroupManager() {
        return isAdmin() || isUserGroupManager();
    }

}
