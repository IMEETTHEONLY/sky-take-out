package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    public final static String url="https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    public User login(String code) {
        String openid=getOpenid(code);

        //判断openid是否为空 如果说为空则登录失败
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //查询数据库是否存在这个openid的用户
        User user=userMapper.getByOpenid(openid);

        //不存在则就注册新用户
        if(user==null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            //将用户插入到数据当中  这里需要将生成的id 返回到user当中 所以说用的xml文件方式进行插入
            userMapper.insert(user);
        }

        //返回这个用户
        return user;

    }

    //将获取openid的方法抽取出来
    public String getOpenid(String code){
        //通过httpclient去发起请求获取openid
        Map<String, String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String result = HttpClientUtil.doGet(url, map);

        //解析json字符串
        JSONObject jsonObject = JSON.parseObject(result);
        String openid=jsonObject.getString("openid");

        return openid;

    }
}
