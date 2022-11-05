package com.hushihao.myzhxy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hushihao.myzhxy.pojo.Clazz;

import java.util.List;

public interface ClazzService extends IService<Clazz> {
    IPage getClazzsByOpr(Page<Clazz> page, Clazz clazz);


    List<Clazz> getClazzs();
}
