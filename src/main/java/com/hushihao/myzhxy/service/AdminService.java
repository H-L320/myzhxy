package com.hushihao.myzhxy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hushihao.myzhxy.pojo.Admin;
import com.hushihao.myzhxy.pojo.LoginForm;

public interface AdminService extends IService<Admin> {
    Admin login(LoginForm loginForm);

    Admin getAdminById(Long userId);

    IPage<Admin> getAdminsByOpr(Page<Admin> pageParam, String adminName);

    //IService是由mybatisplus提供的，里面对应了一些比较基础的增删改查的业务方法，
    //直接继承之后不用我们自己去写大量的增删改查
}
