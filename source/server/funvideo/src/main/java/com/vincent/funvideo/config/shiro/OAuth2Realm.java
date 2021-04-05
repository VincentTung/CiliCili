package com.vincent.funvideo.config.shiro;

import com.vincent.funvideo.db.pojo.User;
import com.vincent.funvideo.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class OAuth2Realm  extends AuthorizingRealm {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }


    /**
     * 授权(验证权限时候调用)
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 查询用户权限列表
        Set<String> permissions = new HashSet<>();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permissions);
        return info;
    }


    /**
     * 认证(登录时候调用)
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        String accessToken = (String) authenticationToken.getPrincipal();
        // 从令牌中获取userId，然后检测该账户是否被冻结。
        int userId = jwtUtil.getUserId(accessToken);
        User user = userService.searchUserInfo(userId);

        if(user == null){//
            throw new LockedAccountException("账号已被锁定，请联系管理员");
        }
        // 往info对象中添加用户信息、Token字符串
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,accessToken,getName());
        return info;
    }
}
