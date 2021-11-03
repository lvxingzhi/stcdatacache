package com.note.stcdatacache.aspect;

import com.note.stcdatacache.annotation.STCDataCache;
import com.note.stcdatacache.annotation.STCDataCacheDelete;
import com.note.stcdatacache.cache.STCCacheLocalTemplate;
import com.note.stcdatacache.cache.STCCacheTemplate;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * STCDataCache处理器
 *
 * @Author xingzhi.lv
 * @Version 2.1
 * @Date 2021/11/3 11:02
 */
@Aspect
@Component
public class STCDataCacheAspect {
    private static final Logger logger = LoggerFactory.getLogger(STCDataCacheAspect.class);
    public static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    public static final String KEY_SPLIT = "_";
    private STCCacheTemplate cacheTemplate = new STCCacheLocalTemplate();
    public static final String STCNULL = "STCNULL";

    @Pointcut("@annotation(com.note.stcdatacache.annotation.STCDataCache)")
    public void methodCachePoint() {
    }

    @Pointcut("@annotation(com.note.stcdatacache.annotation.STCDataCacheDelete)")
    public void methodCacheDeletePoint() {
    }

    @Around(value = "methodCachePoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            STCDataCache cacheAnno = AnnotationUtils.findAnnotation(method, STCDataCache.class);
            String keyEL = cacheAnno.cacheKey();
            Object[] args = joinPoint.getArgs();
            String[] argNames = signature.getParameterNames();
            EvaluationContext context = new StandardEvaluationContext();
            String cacheKey;
            if (Strings.isNotEmpty(keyEL)) {
                if (args != null && args.length > 0) {
                    for (int i = 0; i < argNames.length; i++) {
                        context.setVariable(argNames[i], args[i]);
                    }
                }
                Object obj = EXPRESSION_PARSER.parseExpression(keyEL).getValue(context);
                cacheKey = obj.toString();
            } else {
                String className = joinPoint.getSignature().getDeclaringTypeName();
                String methodName = joinPoint.getSignature().getName();
                cacheKey = className + KEY_SPLIT + methodName;
            }
            String group = cacheAnno.group();
            cacheKey = group + KEY_SPLIT + cacheKey;
            // 读取缓存数据
            Object cacheResult = cacheTemplate.get(cacheKey);
            if (cacheResult != null && !STCNULL.equals(cacheResult)) {
                return cacheResult;
            }
            // 空占位符
            if (STCNULL.equals(cacheResult)) {
                return null;
            }
            final Object result = joinPoint.proceed();
            if (Objects.isNull(result)) {
                cacheTemplate.set(cacheKey, STCNULL);
            } else {
                cacheTemplate.set(cacheKey, result);
            }
            return result;
        } catch (Throwable ta) {
            logger.error("STCDataCache read cache error", ta);
            return joinPoint.proceed();
        }
    }

    @Around(value = "methodCacheDeletePoint()")
    public Object deleteAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            STCDataCacheDelete cacheAnno = AnnotationUtils.findAnnotation(method, STCDataCacheDelete.class);
            String keyEL = cacheAnno.cacheKey();
            Object[] args = joinPoint.getArgs();
            String[] argNames = signature.getParameterNames();
            EvaluationContext context = new StandardEvaluationContext();
            String cacheKey;
            if (Strings.isNotEmpty(keyEL)) {
                if (args != null && args.length > 0) {
                    for (int i = 0; i < argNames.length; i++) {
                        context.setVariable(argNames[i], args[i]);
                    }
                }
                Object obj = EXPRESSION_PARSER.parseExpression(keyEL).getValue(context);
                cacheKey = obj.toString();
            } else {
                String className = joinPoint.getSignature().getDeclaringTypeName();
                String methodName = joinPoint.getSignature().getName();
                cacheKey = className + KEY_SPLIT + methodName;
            }
            String group = cacheAnno.group();
            cacheKey = group + KEY_SPLIT + cacheKey;
            // 删除缓存数据
            cacheTemplate.delete(cacheKey);
        } catch (Throwable ta) {
            logger.error("STCDataCache delete cache error", ta);
        }
        return joinPoint.proceed();
    }

    public STCCacheTemplate getCacheTemplate() {
        return cacheTemplate;
    }

    public void setCacheTemplate(STCCacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }
}
