package com.sharephoto.shiro;

import com.sharephoto.dao.UserMapper;
import com.sharephoto.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class MyRealm extends AuthorizingRealm {

    @Autowired
    UserMapper userMapper;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        String username = userToken.getUsername();
        User user = userMapper.selectLoginVerificationByUsername(username);

        if(user == null){
            throw new UnknownAccountException("用户不存在");
        }
        if(!user.isActive()){
            throw new LockedAccountException("用户被封禁");
        }

        Object principal = username;
        Object credentials = user.getPasswordHash();
        String realmName = getName();
        ByteSource credentialsSalt = ByteSource.Util.bytes(principal);

        SimpleAuthenticationInfo info = null;
        info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
        return info;
    }

    //给Shiro的授权验证提供授权信息
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principalCollection) {

        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        List<String> permissions = null;
        List<String> roles = new ArrayList<>();
        String username= (String) principalCollection.getPrimaryPrincipal();
        String role= userMapper.selectRoleByUsername(username);
        permissions =userMapper.selectPermissions(role);
        roles.add(role);
        info.addRoles(roles);//设置角色
        info.addStringPermissions(permissions);//设置权限
        return info;
    }

    public static String generateHash(String username, String password) {
            String hashAlgorithmName = "MD5";//加密方式
            Object crdentials = password;//密码原值
            ByteSource salt = ByteSource.Util.bytes(username);//以账号作为盐值
            int hashIterations = 1024;//加密1024次
            Object result = new SimpleHash(hashAlgorithmName,crdentials,salt,hashIterations);
            return result.toString();

    }
}