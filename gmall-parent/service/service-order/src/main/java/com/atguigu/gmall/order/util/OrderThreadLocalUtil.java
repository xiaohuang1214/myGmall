package com.atguigu.gmall.order.util;

/**
 * @author 黄梁峰
 * 自定义本地线程工具类
 */
public class OrderThreadLocalUtil {

    /**
     * 初始化本地线程对象
     */
    private final static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 设置用户名
     *
     * @param username
     */
    public static void setUsername(String username) {
        threadLocal.set(username);
    }


    /**
     * 获取用户名
     *
     * @return
     */
    public static String getUsername() {
        return threadLocal.get();
    }
}
