package com.hushihao.myzhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hushihao.myzhxy.pojo.Clazz;
import com.hushihao.myzhxy.service.ClazzService;
import com.hushihao.myzhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "班级管理器")
@RestController   //不是@controller,因为现在前后端都是异步交互，每一个控制器都要加@ResponseBody
@RequestMapping("/sms/clazzController")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    @ApiOperation("查询所有班级信息")
    @GetMapping("/getClazzs")
    public Result getClazzs(){
        List<Clazz> clazzes = clazzService.getClazzs();
        return Result.ok(clazzes);
    }


    @ApiOperation(("删除单个或多个班级"))
    @DeleteMapping("/deleteClazz")
    public Result deleteClazz(
            @ApiParam("要删除的多个班级的id的JSON数组")@RequestBody List<Integer> ids)
    {
        clazzService.removeByIds(ids);
        return Result.ok();
    }

    @ApiOperation(("增加或修改班级信息"))
    @PostMapping("/saveOrUpdateClazz")
    public Result saveOrUpdateClazz(
            @ApiParam("JSON格式的班级信息")@RequestBody Clazz clazz)
    {
        clazzService.saveOrUpdate(clazz);
        return Result.ok();
    }


    @ApiOperation(("分页带条件查询班级信息"))
    @GetMapping("/getClazzsByOpr/{pageNo}/{pageSize}")
    public Result getClazzsByOpr(
            @ApiParam("分页查询的页码数")@PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询的页大小")@PathVariable("pageSize") Integer pageSize,
            @ApiParam("分页查询的查询条件")Clazz clazz
    ){

        Page<Clazz> page = new Page<>(pageNo,pageSize);
        IPage iPage = clazzService.getClazzsByOpr(page,clazz);
        return Result.ok(iPage);
    }

}
