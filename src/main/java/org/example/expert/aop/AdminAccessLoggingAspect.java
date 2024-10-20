package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import org.example.expert.config.JwtUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AdminAccessLoggingAspect {

    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;

    public AdminAccessLoggingAspect(final HttpServletRequest request, final JwtUtil jwtUtil) {
        this.request = request;
        this.jwtUtil = jwtUtil;
    }

    // @Slf4j 를 추가하면 Lombok이 자동으로 log 라는 이름의 Logger 객체를 클래스에 추가합니다.
    // -> private static final Logger logger = LoggerFactory.getLogger(AdminAccessLoggingAspect.class); 가 추가되는것.
    /*
        SLF4J는 로그 메시지를 다섯 가지 주요 로그 레벨로 출력합니다:
        TRACE: 가장 상세한 로그, 보통 개발 중에 세부적인 디버깅을 위해 사용됩니다.
        DEBUG: 디버깅 목적으로 사용되며, 시스템의 흐름을 추적할 때 유용합니다.
        INFO: 정상적인 정보, 시스템의 주요 동작을 로그로 기록합니다.
        WARN: 잠재적 문제나 예외 상황을 로그에 남깁니다.
        ERROR: 오류나 시스템 장애가 발생했을 때 사용됩니다.

        여기서는 INFO 를 사용.
     */

    @Before("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..))")
    public void logAfterDelete(JoinPoint joinPoint){
        logAdminAccess(joinPoint);
    }

    @Before("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void logAfterChangeUserRole(JoinPoint joinPoint){
        logAdminAccess(joinPoint);
    }

    private void logAdminAccess(JoinPoint joinPoint){
        String authHeader = request.getHeader("Authorization");
        String token = jwtUtil.substringToken(authHeader);

        //요청 사용자의 Id
        Claims claims = jwtUtil.extractClaims(token);
        String userId = claims.getSubject();

        //API 요청 시각
        LocalDateTime requestTime = LocalDateTime.now();

        //API 요청 URL
        String requestUrl = request.getRequestURI();

        log.info("Admin Access Log : User Id: {}, Request Time: {}, Request URL: {}, Method : {}",
                userId, requestTime, requestUrl, joinPoint.getSignature().getName());

    }
}
