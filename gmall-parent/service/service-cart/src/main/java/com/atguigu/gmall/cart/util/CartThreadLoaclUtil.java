package com.atguigu.gmall.cart.util;


/**
 * 自定义本地线程工具类
 * @author 黄梁峰
 */
public class CartThreadLoaclUtil {

    private final static ThreadLocal<String> threadLocal = new ThreadLocal();

    /**
     * 设置用户
     * @param username
     */
    public static void setUsername(String username){
        threadLocal.set(username);
    }


    /**
     * 获取用户
     *
     * @return
     */
    public static String getUsername(){
        return threadLocal.get();
    }

}
