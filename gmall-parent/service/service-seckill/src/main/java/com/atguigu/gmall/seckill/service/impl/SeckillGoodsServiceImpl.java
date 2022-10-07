package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author 黄梁峰
 * 秒杀商品相关接口的实现类
 */
@Service
@Transactional
@Log4j2
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 获取当前时段的商品
     *
     * @param time
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoodsList(String time) {
        return (List<SeckillGoods>) redisTemplate.opsForHash().values(time);
    }

    /**
     * 查询指定商品
     *
     * @param time
     * @param goodsId
     * @return
     */
    @Override
    public SeckillGoods getSeckillGoods(String time, String goodsId) {
        return (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
    }

    /**
     * 数据同步
     *
     * @param key
     */
    @Override
    public void mergeSeckillGoodsStockToDb(String key) {
        Set keys = redisTemplate.opsForHash().keys("SeckillStockCount" + key);
        if (!keys.isEmpty()) {

            keys.stream().forEach(goodsId -> {
                try {
                    Integer stock = (Integer) redisTemplate.opsForHash().get("SeckillStockCount" + key, goodsId);
                    //跟新数据库
                    int i = seckillGoodsMapper.updateSeckillGoodsStock(stock, Long.parseLong(goodsId.toString()));
                    if (i < 0) {
                        log.error("库存修改失败,商品的id为:" + goodsId);
                    }
                    //删除redis
                    redisTemplate.opsForHash().delete("SeckillStockCount" + key, goodsId.toString());
                } catch (Exception e) {
                    log.error("库存修改失败,商品的id为:" + goodsId);
                }
            });

        }
    }
}
