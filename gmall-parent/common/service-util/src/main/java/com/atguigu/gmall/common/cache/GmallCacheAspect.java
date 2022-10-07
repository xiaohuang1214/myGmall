package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){

        /*
        1.  获取参数列表
        2.  获取方法上的注解
        3.  获取前缀
        4.  获取目标方法的返回值
         */
        Object result = null;
        try {
            //获取切入点方法的参数
            Object[] args = point.getArgs();
            //获取方法签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            //获取方法指定注解
            GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);
            // 获取注解GmallCache设置的前缀参数
            String prefix = gmallCache.prefix();
            // 从缓存中获取数据  key--->注解设置的前缀参数+方法传入参数
            String key = prefix+Arrays.asList(args).toString();
            // 获取缓存数据
            result = cacheHit(signature, key);
            //判断redis中是否有数据
            if (result!=null){
                // 有直接返回redis中的数据
                return result;
            }
            // 获取锁,这个key需要单独起名以防和上面redis的key冲突
            RLock lock = redissonClient.getLock(key + "lock");
            //尝试加锁
            boolean flag = lock.tryLock(100, 100, TimeUnit.SECONDS);
            if (flag){
               try {
                   //加锁成功
                   try {
                       result = point.proceed(point.getArgs());
                       // 防止缓存穿透
                       if (null==result){
                           // 并把结果放入缓存
                           Object o = new Object();
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(o),100,TimeUnit.SECONDS);
                           return o;
                       }else{
                           //查数据库并放入缓存
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result),24*3600,TimeUnit.SECONDS);
                           return result;
                       }
                   } catch (Throwable throwable) {
                       throwable.printStackTrace();
                   }

               }catch (Exception e){
                   e.printStackTrace();
               }finally {
                   // 释放锁
                   lock.unlock();
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //boolean flag = lock.tryLock(10L, 10L, TimeUnit.SECONDS);
        return result;
    }

    /**
     * 获取缓存数据
     * @param signature 方法签名
     * @param key redis中的key值
     * @return
     */
    private Object cacheHit(MethodSignature signature, String key) {
        // 1. 查询缓存
        String cache = (String)redisTemplate.opsForValue().get(key);
        //判断redis是否有值
        if (StringUtils.isNotBlank(cache)) {
            // 有，则反序列化，直接返回
            // 获取方法返回类型
            Class returnType = signature.getReturnType();
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            //将redis中的json数据转换成对象类型返回
            return JSONObject.parseObject(cache, returnType);
        }
        return null;
    }

}
