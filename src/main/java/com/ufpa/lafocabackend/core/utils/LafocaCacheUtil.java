package com.ufpa.lafocabackend.core.utils;

import com.ufpa.lafocabackend.domain.model.AllSystem;
import com.ufpa.lafocabackend.domain.model.dto.HostInfo;
import com.ufpa.lafocabackend.domain.model.dto.YearClassDTO;
import com.ufpa.lafocabackend.domain.model.dto.input.RecordCountDTO;
import com.ufpa.lafocabackend.domain.model.dto.output.LafocaDto;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class LafocaCacheUtil {
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
    private static final long CACHE_MAX_AGE_LOGIN_SESSION = 5;
    private static final long CACHE_MAX_AGE_HEALTH_CHECK = 30;
    private static final long CACHE_MAX_AGE_HOST_CHECK = 30;
    private static final long CACHE_MAX_AGE_LAFOCA_INFO = 30;
    private static final long CACHE_MAX_AGE_CLASSES = 30;

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

    public static <T> ResponseEntity<T> createCachedResponseLoginSession(T body) {
        return createCachedResponse(body, CACHE_MAX_AGE_LOGIN_SESSION);
    }


    public static ResponseEntity<RecordCountDTO> createCachedResponseNoContentRecordCount() {
        return ResponseEntity.noContent()
                .cacheControl(CacheControl.maxAge(CACHE_MAX_AGE_RECORD_COUNT, TimeUnit.SECONDS))
                .build();
    }

    public static ResponseEntity<String> createCachedResponseHealthCheck(String s) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(CACHE_MAX_AGE_HEALTH_CHECK, TimeUnit.SECONDS))
                .body(s);
    }

    public static <T> ResponseEntity<T> createCachedResponseHostCheck(T body) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(CACHE_MAX_AGE_HOST_CHECK, TimeUnit.SECONDS))
                .body(body);
    }

    public static  <T> ResponseEntity<T> createCachedResponseLafocaInfo(T body) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(CACHE_MAX_AGE_LAFOCA_INFO, TimeUnit.SECONDS))
                .body(body);
    }

    public static <T> ResponseEntity<T> createCachedResponseClasses(T body) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(CACHE_MAX_AGE_CLASSES, TimeUnit.SECONDS))
                .body(body);
    }
}
