package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.Java0217Cache;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 黄梁峰
 * <p>
 * 首页三级分类导航
 */
@Service
public class IndexServiceImpl implements IndexService {
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;

    /**
     * 查询首页三级分类导航
     *
     * @return
     */
    @Override
    @Java0217Cache(prefix = "getIndexCategory")
    public List<JSONObject> getIndexCategory() {

        //查询所有一二三级分类信息
        List<BaseCategoryView> categoryList = baseCategoryViewMapper.selectList(null);
        //根据一级分类进行分组
        Map<Long, List<BaseCategoryView>> category1Map = categoryList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        return category1Map.entrySet().stream().map(category1 -> {
            JSONObject category1Json = new JSONObject();
            //获取一级分类的Id
            Long category1Id = category1.getKey();
            //获取一级分类对应的二级分类的集合
            List<BaseCategoryView> category2List = category1.getValue();
            //根据二级分类进行分组
            Map<Long, List<BaseCategoryView>> category2Map = category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //二级分类集合
            List<JSONObject> category2NameList = category2Map.entrySet().stream().map(category2 -> {
                JSONObject category2Json = new JSONObject();
                //获取二级分类的Id
                Long category2Id = category2.getKey();
                //获取二级分类对应的三级分类的集合
                List<BaseCategoryView> category3List = category2.getValue();
                //三级分类集合
                List<JSONObject> category3NameList = category3List.stream().map(category3 -> {
                    JSONObject category3Json = new JSONObject();
                    category3Json.put("categoryId", category3.getCategory3Id());
                    category3Json.put("category3Name", category3.getCategory3Name());
                    return category3Json;
                }).collect(Collectors.toList());

                category2Json.put("categoryId", category2Id);
                category2Json.put("category2Name", category3List.get(0).getCategory2Name());
                category2Json.put("category3List", category3NameList);
                return category2Json;
            }).collect(Collectors.toList());

            category1Json.put("categoryId", category1Id);
            category1Json.put("category1Name", category2List.get(0).getCategory1Name());
            category1Json.put("category2List", category2NameList);
            return category1Json;
        }).collect(Collectors.toList());

    }
}
