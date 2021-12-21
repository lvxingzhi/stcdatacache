

>详细文档地址: https://lvxingzhi.github.io/2021/11/03/4-%5B%E9%A1%B9%E7%9B%AE%5DSTC-stcdatacache/

## 系统实现原理
>利用Spring 提供的AOP机制,实现对方法的扩展.
>利用Spel表达式实现方法入参与缓存key动态匹配.
>预定义缓存接口,实现对不同缓存方式的兼容.
>异常处理保证业务的可用性.
>整理并不复杂, 流程图略.


## 依赖
>1, Spring
>2, 任意分布式缓存
<!--more-->

## 目录结构
>annotation: 注解
>aspect: 注解处理器
>cache: 缓存接口定义和本地实现案例
>test: 测试Demo


## 核心代码
```bash
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
	// 缓存切面
    @Pointcut("@annotation(com.note.stcdatacache.annotation.STCDataCache)")
    public void methodCachePoint() {
    }
	
	// 删除缓存切面
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
			// 使用Spel解析
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
			// 使用Spel解析
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
```



## 测试
```C
@SpringBootTest
class StcdatacacheApplicationTests {

    @Resource
    private DemoService demoService;

    @Test
    void contextLoads() {
        // 无缓存
        String demoName1 = demoService.findDemoName(58);
        System.out.println(demoName1);
        // 查缓存
        String demoName2 = demoService.findDemoName(58);
        System.out.println(demoName2);
        // 删除缓存
        demoService.deleteDemoDelete(58);
        // 无缓存
        String demoName3 = demoService.findDemoName(58);
        System.out.println(demoName3);
    }

}
```
### 结果 
```C
DemoName : -2094750780
DemoName : -2094750780 // 读取缓存
DemoName : 336840328 // 缓存清空后重新获取
```




