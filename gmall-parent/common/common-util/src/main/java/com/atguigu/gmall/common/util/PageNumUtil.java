package com.atguigu.gmall.common.util;

/**
 * @author 黄梁峰
 *
 * 页码计算器
 */
public class PageNumUtil {
    public static int getPage(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            return i>0?i:1;
        }catch (Exception e){
            return 1;
        }
    }
}
