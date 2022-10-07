package com.atguigu.gmall.common.cache;


import java.lang.annotation.*;

/**
 * @author 黄梁峰
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Java0217Cache {
    String prefix() default "cache";
}
