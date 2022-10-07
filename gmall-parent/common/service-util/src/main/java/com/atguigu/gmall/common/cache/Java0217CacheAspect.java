package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.RedisConst;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄梁峰
 */
@Component
@Aspect
public class Java0217CacheAspect {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.common.cache.Java0217Cache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point) {

        //初始化返回结果
        Object result = null;
        try {
            //获取切入点方法的参数列表
            Object[] args = point.getArgs();
            //获取方法签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            Java0217Cache java0217Cache = signature.getMethod().getAnnotation(Java0217Cache.class);
            String prefix = java0217Cache.prefix();
            //设置redis的key值
            String key = RedisConst.SKUKEY_PREFIX + prefix + Arrays.asList(args);

            //查询redis中是否有值
            String cache = (String) redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(cache)) {
                //将获取的redis中的json转换成对象类型
                result = JSONObject.parseObject(cache, signature.getReturnType());
            }
            if (result != null) {
                //有值则直接返回
                return result;
            }
            //redis中没有值,查询数据库
            //获取锁
            RLock lock = redissonClient.getLock(key + RedisConst.SKULOCK_SUFFIX);
            //尝试加锁
            try {
                if (lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.MINUTES)) {
                    //进来加锁成功
                    try {
                        //查询数据库
                        result = point.proceed(args);
                        //数据库中没有,redis中也没有,防止穿透
                        if (result == null) {
                            //创建一个对象
                            Object o = new Object();
                            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(o), RedisConst.SKULOCK_EXPIRE_PX3, TimeUnit.SECONDS);
                            return o;
                        } else {
                            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                            return result;
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    } finally {
                        //释放锁
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
