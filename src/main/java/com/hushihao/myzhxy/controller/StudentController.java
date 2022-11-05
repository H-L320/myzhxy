package com.hushihao.myzhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hushihao.myzhxy.pojo.Student;
import com.hushihao.myzhxy.service.StudentService;
import com.hushihao.myzhxy.util.MD5;
import com.hushihao.myzhxy.util.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController   //不是@controller,因为现在前后端都是异步交互，每一个控制器都要加@ResponseBody
@RequestMapping("/sms/studentController")
public class StudentController {


    @Autowired
    private StudentService studentService;


    @DeleteMapping("/delStudentById")
    public Result delStudentById(
            @ApiParam("要删除的JSON学生信息")@RequestBody List<Integer> ids
    ){
        studentService.removeByIds(ids);
        return Result.ok();
    }


    @ApiOperation("保存或修改学生信息")
    @PostMapping("/addOrUpdateStudent")
    public Result addOrUpdateStudent(
            @ApiParam("要修改或保存的JSON学生信息")@RequestBody Student student
    ){
        Integer id = student.getId();
        if (null == id || 0 == id) {
            student.setPassword(MD5.encrypt(student.getPassword()));
        }
        studentService.saveOrUpdate(student);
        return Result.ok();
    }


    @ApiOperation("分页带条件查询学生信息")
    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudentByOpr(
            @ApiParam("页码数")@PathVariable("pageNo") Integer pageNo,
            @ApiParam("页大小")@PathVariable("pageSize") Integer pageSize,
            @ApiParam("查询的条件") Student student
    ){
        //分页信息封装Page对象
        Page<Student> pageParam = new Page(pageNo,pageSize);

        IPage<Student> studentIPage = studentService.getStudentByOpr(pageParam,student);
        //封装Result返回
        return Result.ok(studentIPage);
    }

}
