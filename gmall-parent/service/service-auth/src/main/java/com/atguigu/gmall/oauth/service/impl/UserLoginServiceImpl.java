package com.atguigu.gmall.oauth.service.impl;

import com.atguigu.gmall.oauth.service.UserLoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * @author 黄梁峰
 *
 * 登录的实现类
 */
@Service
public class UserLoginServiceImpl implements UserLoginService {
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private LoadBalancerClient loadBalancerClient;

    /**
     * 登录并获取token
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public AuthToken login(String username, String password) {
        //动态获uri
        ServiceInstance serviceInstance = loadBalancerClient.choose("service-oauth");
        URI uri = serviceInstance.getUri();
        //获取url
        String url = uri + "/oauth/token";
        //拼接请求头
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization",getHeaders());
        //包装请求体
        MultiValueMap<String, String> body = new HttpHeaders();
        body.add("username",username);
        body.add("password",password);
        body.add("grant_type","password");
        HttpEntity<Object> entity = new HttpEntity<>(body,headers);
        //发起post请求
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        //获取响应体
        Map<String, String> result = response.getBody();
        //判断是否有数据
        if (result == null){
            return null;
        }
        //封装令牌属性
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(result.get("access_token"));
        authToken.setRefreshToken(result.get("refresh_token"));
        authToken.setJti(result.get("jti"));

        return authToken;
    }

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;

    /**
     * 拼接请求头
     *
     * @return
     */
    private String getHeaders(){
        //拼接请求头
        String headers = clientId + ":" + clientSecret;
        //对请求头进行编码
        byte[] encode = Base64.getEncoder().encode(headers.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encode);
    }

}
