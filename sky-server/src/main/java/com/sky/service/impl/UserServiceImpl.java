package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    // 微信登录接口地址
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     * @param userLoginDTO 前端传来的登录信息（code）
     * @return 登录成功的用户对象
     */
    @Override
    public User weLogin(UserLoginDTO userLoginDTO) {
        // 1. 调用微信接口，获取 openid
        //Map<String, String> map = new HashMap<>();
        //map.put("appid", weChatProperties.getAppid());
        //map.put("secret", weChatProperties.getSecret());
        //map.put("js_code", userLoginDTO.getCode());
        //map.put("grant_type", "authorization_code");

        //String responseJson = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        //log.info("微信登录接口返回：{}", responseJson);

        // 2. 解析返回结果，获取 openid
        //com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(responseJson);
        //String openid = jsonObject.getString("openid");

        String openid = getOpenid(userLoginDTO.getCode());


        // 3. 判断 openid 是否获取成功
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 4. 根据 openid 查询用户是否已存在
        User user = userMapper.getByOpenid(openid);

        // 5. 如果是新用户，自动完成注册
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        return user;
    }

    /*
    调用微信接口服务,获取微信用户的 openid
     */
    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        String responseJson = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        log.info("微信登录接口返回：{}", responseJson);

        // 2. 解析返回结果，获取 openid
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(responseJson);
        String openid = jsonObject.getString("openid");
        return openid;
    }

}