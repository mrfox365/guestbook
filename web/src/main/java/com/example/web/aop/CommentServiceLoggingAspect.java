package com.example.web.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CommentServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceLoggingAspect.class);

    // Вказуємо шлях до методу delete у вашому Core модулі
    @Around("execution(* com.example.core.service.CommentService.delete(..))")
    public Object logDeleteCall(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object[] args = pjp.getArgs();

        Long bookId = args.length > 0 ? (Long) args[0] : null;
        Long commentId = args.length > 1 ? (Long) args[1] : null;

        log.info("AOP: Calling delete comment (bookId={}, commentId={})", bookId, commentId);

        try {
            Object result = pjp.proceed();
            long time = System.currentTimeMillis() - start;
            log.info("AOP: Success. Execution time: {} ms", time);
            return result;
        } catch (Exception ex) {
            long time = System.currentTimeMillis() - start;
            log.warn("AOP: Failed after {} ms. Reason: {}", time, ex.getMessage());
            throw ex;
        }
    }
}