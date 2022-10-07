package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.util.PageNumUtil;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 黄梁峰
 * <p>
 * 搜索相关接口
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 搜索
     *
     * @param searchData
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchData) {
        try {
            //拼接条件
            SearchRequest searchRequest = buildSearchParams(searchData);
            //执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析查询条件并返回
            return getSearchResult(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 拼接查询条件
     *
     * @param searchData
     * @return
     */
    private SearchRequest buildSearchParams(Map<String, String> searchData) {
        //拼接查询条件
        SearchRequest searchRequest = new SearchRequest("goods_java0107");
        //初始化查询条件构造
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //定义组合条件查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //关键字不为空作为查询条件
        String keywords = searchData.get("keywords");
        if (!StringUtils.isEmpty(keywords)) {
            boolQuery.must(QueryBuilders.matchQuery("title", keywords));
        }
        //拼接品牌查询条件 格式:1:华为
        String tradeMark = searchData.get("tradeMark");
        if (!StringUtils.isEmpty(tradeMark)) {
            String[] split = tradeMark.split(":");
            boolQuery.must(QueryBuilders.termQuery("tmId", split[0]));
        }
        //拼接平台属性查询条件 格式: attr_平台属性名 = 1:电信5G
        //获取所有参数
        searchData.entrySet().stream().forEach(param -> {
            String key = param.getKey();
            if (key.startsWith("attr_")) {
                //获取参数值
                String value = param.getValue();
                String[] split = value.split(":");
                BoolQueryBuilder nestedQuery = QueryBuilders.boolQuery();
                //平台属性id要相等
                nestedQuery.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                //平台属性值也要相等
                nestedQuery.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                //Nested 支持嵌套查询
                boolQuery.must(QueryBuilders.nestedQuery("attrs", nestedQuery, ScoreMode.None));
            }
        });
        //拼接价格查询->参数格式 price = 0-500元  price = 3000元以上
        String price = searchData.get("price");
        if (!StringUtils.isEmpty(price)) {
            String[] split = price.replace("元", "")
                    .replace("以上", "")
                    .split("-");
            if (split.length == 2) {
                //0-500元
                boolQuery.must(QueryBuilders.rangeQuery("price")
                        .gte(split[0])
                        .lt(split[1]));
            } else {
                //3000元以上
                boolQuery.must(QueryBuilders.rangeQuery("price")
                        .gte(split[0]));
            }

        }
        builder.query(boolQuery);

        //排序 sort=类型:排序规则
        String sort = searchData.get("sort");
        if (sort != null){
            String[] split = sort.split(":");
            SortOrder sortOrder = SortOrder.valueOf(split[1]);
            if (!StringUtils.isEmpty(split[0]) &&
                    !StringUtils.isEmpty(split[1])) {
                builder.sort(split[0], sortOrder);
            }
        } else {
            //默认排序
            builder.sort("id", SortOrder.DESC);
        }

        //分页 参数格式:pageNum=1
        String pageNum = searchData.get("pageNum");
        //页码
        int current = PageNumUtil.getPage(pageNum);
        //0-49   50-99   100-149
        builder.from((current-1)*50)
                .size(50);

        //高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style=color:red>");
        highlightBuilder.postTags("</font>");
        //设置高亮条件
        builder.highlighter(highlightBuilder);

        //设置品牌的聚合条件
        builder.aggregation(AggregationBuilders.terms("aggTmId").field("tmId")
                .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl")));
        //设置平台属性的聚合条件
        builder.aggregation(AggregationBuilders.nested("aggAttrs", "attrs")
                        .subAggregation(AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                                .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))))
                .size(100);

        //指定条件
        searchRequest.source(builder);
        //返回条件
        return searchRequest;
    }

    /**
     * 解析搜索结果
     *
     * @param searchResponse
     * @return
     */
    private Map<String, Object> getSearchResult(SearchResponse searchResponse) {
        //初始化返回结果
        Map<String, Object> result = new HashMap<>();
        //获取命中数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //获取总命中数
        long totalHits = hits.getTotalHits();
        result.put("totalHits", totalHits);
        //商品列表初始化
        List<Goods> goodsList = new ArrayList<>();
        //遍历每条数据
        while (iterator.hasNext()) {
            //每一条数据
            SearchHit next = iterator.next();
            //获取每条文档的json字符串类型的数据
            String sourceAsString = next.getSourceAsString();
            //反序列化
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            //提取高亮的数据
            HighlightField highlightField = next.getHighlightFields().get("title");
            if (highlightField != null) {
                Text[] fragments = highlightField.getFragments();
                if (fragments != null && fragments.length > 0) {
                    String content = "";
                    for (Text fragment : fragments) {
                        content += fragment;
                    }
                    goods.setTitle(content);
                }
            }

            //保存数据
            goodsList.add(goods);
        }
        result.put("goodsList", goodsList);
        //获取品牌聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        //解析品牌聚合结果
        List<SearchResponseTmVo> responseTmVoList = getTmAggResult(aggregations);
        result.put("responseTmVoList", responseTmVoList);
        //解析品牌属性结果
        List<SearchResponseAttrVo> responseAttrVoList = getAttrInfoAggResult(aggregations);
        result.put("responseAttrVoList", responseAttrVoList);


        return result;

    }

    /**
     * 解析平台属性的聚合结果
     *
     * @param aggregations
     * @return
     */
    private List<SearchResponseAttrVo> getAttrInfoAggResult(Aggregations aggregations) {
        ParsedNested aggAttrs = aggregations.get("aggAttrs");
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        return aggAttrId.getBuckets().stream().map(bucket -> {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取平台属性id
            long attrId = bucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取平台属性名
            ParsedStringTerms aggAttrName = bucket.getAggregations().get("aggAttrName");
            if (!aggAttrName.getBuckets().isEmpty()) {
                String attrName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取平台属性值
            ParsedStringTerms aggAttrValue = bucket.getAggregations().get("aggAttrValue");
            if (!aggAttrValue.getBuckets().isEmpty()) {
                List<String> attrValueList = aggAttrValue.getBuckets().stream().map(value -> {
                    //获取每一个平台属性值并返回
                    return value.getKeyAsString();
                }).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValueList(attrValueList);
            }
            return searchResponseAttrVo;
        }).collect(Collectors.toList());

    }

    /**
     * 解析品牌聚合
     *
     * @param aggregations
     * @return
     */
    private List<SearchResponseTmVo> getTmAggResult(Aggregations aggregations) {
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");
        return aggTmId.getBuckets().stream().map(bucket -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌Id
            long tmId = bucket.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //获取品牌名
            ParsedStringTerms aggTmName = bucket.getAggregations().get("aggTmName");
            if (!aggTmName.getBuckets().isEmpty()) {
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            //获取品牌url
            ParsedStringTerms aggTmLogoUrl = bucket.getAggregations().get("aggTmLogoUrl");
            if (!aggTmLogoUrl.getBuckets().isEmpty()) {
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            return searchResponseTmVo;
        }).collect(Collectors.toList());

    }
}
