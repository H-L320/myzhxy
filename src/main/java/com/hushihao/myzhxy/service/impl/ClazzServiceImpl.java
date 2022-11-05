package com.hushihao.myzhxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hushihao.myzhxy.mapper.ClazzMapper;
import com.hushihao.myzhxy.pojo.Clazz;
import com.hushihao.myzhxy.pojo.Grade;
import com.hushihao.myzhxy.service.ClazzService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("clazzServiceImpl")
@Transactional
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {

    @Override
    public IPage getClazzsByOpr(Page<Clazz> pageParam, Clazz clazz) {
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
        String gradeName = clazz.getGradeName();
        if (!StringUtils.isEmpty(gradeName)) {
            queryWrapper.like("grade_name",gradeName);
        }

        String name = clazz.getName();
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("name",name);
        }

        queryWrapper.orderByDesc("id");
        Page<Clazz> clazzPage = baseMapper.selectPage(pageParam,queryWrapper);
        return clazzPage;
    }

    @Override
    public List<Clazz> getClazzs() {

        return baseMapper.selectList(null);
    }
}
