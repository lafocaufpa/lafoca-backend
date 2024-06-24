package com.ufpa.lafocabackend.core.utils;

import com.ufpa.lafocabackend.domain.model.dto.input.RecordCountDTO;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.TimeUnit;

public class CacheUtil {
    private static final long CACHE_MAX_AGE_GROUP = 30;
    private static final long CACHE_MAX_AGE_USER = 60;
    private static final long CACHE_MAX_AGE_ARTICLE = 60;
    private static final long CACHE_MAX_AGE_FUNCTION_MEMBER = 45;
    private static final long CACHE_MAX_AGE_LINE_OF_RESEARCH = 50;
    private static final long CACHE_MAX_AGE_MEMBER = 60;
    private static final long CACHE_MAX_AGE_NEWS = 60;
    private static final long CACHE_MAX_AGE_PERMISSION = 60;
    private static final long CACHE_MAX_AGE_PROJECT = 60;
    private static final long CACHE_MAX_AGE_RECORD_COUNT = 60;
    private static final long CACHE_MAX_AGE_SKILL = 60;
    private static final long CACHE_MAX_AGE_TCC = 60;


    public static <T> ResponseEntity<T> createCachedResponse(T body, long maxAge) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(maxAge, TimeUnit.SECONDS))
                .body(body);
    }

    public static <T> ResponseEntity<T> createCachedResponseGroup(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_GROUP);
    }

    public static <T> ResponseEntity<T> createCachedResponseUser(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_USER);
    }

    public static <T> ResponseEntity<T> createCachedResponseArticle(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_ARTICLE);
    }

    public static <T> ResponseEntity<T> createCachedResponseFunctionMember(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_FUNCTION_MEMBER);
    }

    public static <T> ResponseEntity<T> createCachedResponseLineOfResearch(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_LINE_OF_RESEARCH);
    }

    public static <T> ResponseEntity<T> createCachedResponseMember(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_MEMBER);
    }

    public static <T> ResponseEntity<T> createCachedResponseNews(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_NEWS);
    }

    public static <T> ResponseEntity<T> createCachedResponsePermission(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_PERMISSION);
    }

    public static <T> ResponseEntity<T> createCachedResponseProject(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_PROJECT);
    }

    public static <T> ResponseEntity<T> createCachedResponseRecordCount(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_RECORD_COUNT);
    }

    public static <T> ResponseEntity<T> createCachedResponseSkill(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_SKILL);
    }

    public static <T> ResponseEntity<T> createCachedResponseTcc(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_TCC);
    }

    public static ResponseEntity<RecordCountDTO> createCachedResponseNoContentRecordCount() {
        return ResponseEntity.noContent()
                .cacheControl(CacheControl.maxAge(CACHE_MAX_AGE_RECORD_COUNT, TimeUnit.SECONDS))
                .build();
    }

}
