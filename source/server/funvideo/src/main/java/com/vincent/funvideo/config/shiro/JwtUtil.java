package com.vincent.funvideo.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vincent.funvideo.util.VideoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 根据userId 生成token
 */
@Component
@Slf4j
public class JwtUtil {
    private static final String USER_ID = "userId";
    //密钥
    @Value("${video.jwt.secret}") //引入yml中配置的值
    private String secret;
    //过期时间（天）
    @Value("${video.jwt.expire}")
    private int expire;

    @Value("${video.jwt.apikey}")
    private String apikey;

    public String createToken(int userId) {
        Date date = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, expire).toJdkDate();
        Algorithm algorithm = Algorithm.HMAC256(secret); //创建加密算法对象
        JWTCreator.Builder builder = JWT.create();
        String token = builder.withClaim(USER_ID, userId).withExpiresAt(date).sign(algorithm);
        return token;
    }



    public boolean checkApiKey(String key){

        if(key.equals(apikey)) return true;
        throw  new VideoException("apikey无效");
    }


    public int getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(USER_ID).asInt();
        } catch (Exception e) {
            throw new VideoException("令牌无效");
        }
    }

    public void verifierToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret); //创建加密算法对象
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
    }
}