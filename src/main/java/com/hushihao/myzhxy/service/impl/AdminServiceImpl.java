package com.hushihao.myzhxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hushihao.myzhxy.mapper.AdminMapper;
import com.hushihao.myzhxy.pojo.Admin;
import com.hushihao.myzhxy.pojo.LoginForm;
import com.hushihao.myzhxy.service.AdminService;
import com.hushihao.myzhxy.util.MD5;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("adminServiceImpl")//当前实现类的id
@Transactional
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Override
    public Admin login(LoginForm loginForm) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", loginForm.getUsername());
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));

        Admin admin = baseMapper.selectOne(queryWrapper);  //根据 entity 条件，查询一条记录
        return admin;
    }

    @Override
    public Admin getAdminById(Long userId) {

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userId);
        Admin admin = baseMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public IPage<Admin> getAdminsByOpr(Page<Admin> pageParam, String adminName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if(!StringUtils.isEmpty(adminName)){
            queryWrapper.like("name",adminName);
        }
        queryWrapper.orderByDesc("id");
        queryWrapper.orderByAsc("name");
        Page page = baseMapper.selectPage(pageParam, queryWrapper);
        return page;

    }
    //AdminService继承于IService,IService里面有很多的抽象方法，需要我们的实现类去实现，
    //靠我们自己去实现太麻烦了，可以考虑去继承ServiceImpl,这个类里面就包含了针对于IService的实现。
}
