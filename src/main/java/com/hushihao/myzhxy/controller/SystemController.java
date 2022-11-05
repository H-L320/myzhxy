package com.hushihao.myzhxy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hushihao.myzhxy.pojo.Admin;
import com.hushihao.myzhxy.pojo.LoginForm;
import com.hushihao.myzhxy.pojo.Student;
import com.hushihao.myzhxy.pojo.Teacher;
import com.hushihao.myzhxy.service.AdminService;
import com.hushihao.myzhxy.service.StudentService;
import com.hushihao.myzhxy.service.TeacherService;
import com.hushihao.myzhxy.util.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController   //不是@controller,因为现在前后端都是异步交互，每一个控制器都要加@ResponseBody
@RequestMapping("/sms/system")
public class SystemController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;


    @ApiOperation("文件上传统一入口")
    @PostMapping("/updatePwd/{oldPwd}/{oldPwd}")
    public Result updatePwd(
            @RequestHeader("token") String token,
            @PathVariable("oldPwd") String oldPwd,
            @PathVariable("oldPwd") String newPwd
    ){

        boolean expiration = JwtHelper.isExpiration(token);
        if (expiration) {
            return Result.fail().message("token失效，请重新登陆后修改密码");
        }
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        oldPwd= MD5.encrypt(oldPwd);
        newPwd= MD5.encrypt(newPwd);

        switch (userType){
            case 1:
                QueryWrapper<Admin> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("id",userId.intValue());
                queryWrapper1.eq("password",oldPwd);
                Admin admin = adminService.getOne(queryWrapper1);
                if (admin != null) {
                    //修改
                    admin.setPassword(newPwd);
                    adminService.saveOrUpdate(admin);
                }else{
                    return Result.fail().message("原密码有误！");
                }
                break;

            case 2:
                QueryWrapper<Student> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("id",userId.intValue());
                queryWrapper2.eq("password",oldPwd);
                Student student = studentService.getOne(queryWrapper2);
                if (student != null) {
                    //修改
                    student.setPassword(newPwd);
                    studentService.saveOrUpdate(student);
                }else{
                    return Result.fail().message("原密码有误！");
                }
                break;

            case 3:
                QueryWrapper<Teacher> queryWrapper3 = new QueryWrapper<>();
                queryWrapper3.eq("id",userId.intValue());
                queryWrapper3.eq("password",oldPwd);
                Teacher teacher = teacherService.getOne(queryWrapper3);
                if (teacher != null) {
                    //修改
                    teacher.setPassword(newPwd);
                    teacherService.saveOrUpdate(teacher);
                }else{
                    return Result.fail().message("原密码有误！");
                }
                break;
        }
        return Result.ok();
    }


    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(
            @ApiParam("头像文件") @RequestPart MultipartFile multipartFile,
            HttpServletRequest request
    ){

        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();

        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String newFileName = uuid + originalFilename.substring(i);
        //保存文件 将文件发送到第三方/独立的图片服务器上

        String portraitPath = "E:/code/myzhxy2/target/classes/public/upload/"+newFileName;
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //响应图片路径
        String path = "upload/"+newFileName;

        return Result.ok(path);
    }


    @GetMapping("/getInfo")
    public Result getInfoByToken(@RequestHeader("token") String token){

        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        //从token中解析出用户id和用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        Map<String,Object> map = new LinkedHashMap<>();
        switch(userType){
            case 1:
                Admin admin = adminService.getAdminById(userId);
                map.put("userType",1);
                map.put("user",admin);
                break;
            case 2:
                Student student = studentService.getStudentById(userId);
                map.put("userType",2);
                map.put("user",student);
                break;
            case 3:
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("userType",3);
                map.put("user",teacher);
                break;
        }
        return Result.ok(map);
    }



    @PostMapping("/login")
    public Result login(@RequestBody LoginForm loginForm, HttpServletRequest request){
        //验证码校验
        HttpSession session = request.getSession();
        String sessionVerifiCode = (String)session.getAttribute("verifiCode");
        String loginVerifiCode = loginForm.getVerifiCode();
        if("".equals(sessionVerifiCode) || null == sessionVerifiCode){
            return Result.fail().message("验证码失效，请刷新后重试");
        }
        if(!sessionVerifiCode.equalsIgnoreCase(loginVerifiCode)){
            return Result.fail().message("验证码有误，请重新输入");
        }
        //从session域移除现有验证码
        session.removeAttribute("verifiCode");
        //分用户类型进行校验


        //准备一个map用户存放响应数据
        Map<String,Object> map = new LinkedHashMap<>();
        switch (loginForm.getUserType()){
            case 1:
                try {
                    Admin admin = adminService.login(loginForm);
                    if (null != admin) {
                        //用户的类型和用户id转换成密文，以token的形式向客户端反馈
                        //String token = JwtHelper.createToken(admin.getId().longValue(), 1);
                        map.put("token", JwtHelper.createToken(admin.getId().longValue(), 1));
                    }else{
                        throw new RuntimeException("用户名或者密码错误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }

            case 2:
                try {
                    Student student = studentService.login(loginForm);
                    if (null != student) {
                        //用户的类型和用户id转换成密文，以token的形式向客户端反馈
                        String token = JwtHelper.createToken(student.getId().longValue(), 2);
                        map.put("token",token);
                    }else{
                        throw new RuntimeException("用户名或者密码错误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }

            case 3:
                try {
                    Teacher teacher = teacherService.login(loginForm);
                    if (null != teacher) {
                        //用户的类型和用户id转换成密文，以token的形式向客户端反馈
                        String token = JwtHelper.createToken(teacher.getId().longValue(), 3);
                        map.put("token",token);
                    }else{
                        throw new RuntimeException("用户名或者密码错误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
        }
        return Result.fail().message("查无此用户");

    }


    
    @GetMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response){
        //获取图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
        //获取图片上的验证码
        String verifiCode = new String(CreateVerifiCodeImage.getVerifiCode());
        //将验证码文本放入session域，为下一次验证做准备
        HttpSession session=request.getSession();
        session.setAttribute("verifiCode",verifiCode);
        //将验证码图片响应给浏览器

        try {
            ImageIO.write(verifiCodeImage,"JPEG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
