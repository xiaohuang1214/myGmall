package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.WxPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 黄梁峰
 * 微信支付管理控制层
 */
@RestController
@RequestMapping(value = "/wx/pay")
public class WxPayController {
    @Resource
    private WxPayService wxPayService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 获取支付地址
     *
     * @param param
     * @return
     */
    @GetMapping(value = "/getPayUrl")
    public String getPayOrder(@RequestParam Map<String, String> param){
        return wxPayService.getPayOrder(param);
    }


    /**
     * 根据订单号查询支付信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getPayResult")
    public String getPayResult(String orderId){
        return wxPayService.getPayResult(orderId);
    }


    /**
     * 给微信调用的获取支付结果的异步通知接口
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/callback/notify")
    public String orderNotify(HttpServletRequest request) throws Exception{
        //获取微信支付数据流
        ServletInputStream inputStream = request.getInputStream();
        //读取数据流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //定义缓冲区
        byte[] bytes = new byte[1024];
        //定义数据长度
        int len = bytes.length;
        //读取数据
        while ((len = inputStream.read(bytes)) != -1){
            outputStream.write(bytes, 0, len);
        }
        //获取输出流字节码
        byte[] byteArray = outputStream.toByteArray();
        //将字节码转成字符串
        String xmlString = new String(byteArray);
        //xml转成map
        Map<String, String> result = WXPayUtil.xmlToMap(xmlString);
        //存储支付渠道:1-微信支付
        result.put("payway","1");
        System.out.println(JSONObject.toJSONString(result));
//        String result = "{\"transaction_id\":\"4200001578202208139726456625\",\"nonce_str\":\"c89fd56bb8ec43b5b83794eac9db76ec\",\"bank_type\":\"OTHERS\",\"openid\":\"oHwsHuALTT8zmpAOrCexFRdcvHec\",\"sign\":\"03593FAF945FC19E14E3971D0FD5C5C8\",\"payway\":\"1\",\"fee_type\":\"CNY\",\"mch_id\":\"1558950191\",\"cash_fee\":\"1\",\"out_trade_no\":\"268\",\"appid\":\"wx74862e0dfcf69954\",\"total_fee\":\"1\",\"trade_type\":\"NATIVE\",\"result_code\":\"SUCCESS\",\"time_end\":\"20220813153524\",\"is_subscribe\":\"N\",\"return_code\":\"SUCCESS\"}\n";
        //获取附加参数
        String attach = result.get("attach");
        Map<String, String> param = JSONObject.parseObject(attach, Map.class);
        rabbitTemplate.convertAndSend(param.get("exchange"), param.get("routingKey"), result);
        Map<String, String> wxResult = new HashMap<>();
        wxResult.put("return_code","SUCCESS");
        wxResult.put("return_msg","OK");
        return WXPayUtil.mapToXml(wxResult);
    }

    /**
     * 关闭订单
     *
     * @param orderId
     * @return
     */
    @GetMapping("/closeOrder/{orderId}")
    public String closeOrder(@PathVariable("orderId") String orderId){
        return wxPayService.closeOrder(orderId);
    }


}
