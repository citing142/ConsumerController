package com.zct0418.action;

import com.zct0418.service.ConsumerService;
import com.zct0418.entity.Consumer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

//业务控制器
@Controller
@RequestMapping("/consumer") //信息表名称
public class ConsumerController {
    private Logger logger = Logger.getLogger(ConsumerController.class);

    @Autowired  //框架会到容器中 寻找对应类型的对象 并装配 == 让 ConsumerService指向那个对象
    ConsumerService consumerService;

//     访问 /consumer 默认到这里
    @RequestMapping()
    String select() throws ServletException, IOException {
        System.out.println("默认select:===========转到consumerform.jsp===============" );
        return "consumerform";
    }

    @RequestMapping(value = "/page")
//    @ResponseBody
        //会将返回的数据以JSON格式返回
    void page(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String uname = request.getParameter("uname");   if (uname == null) uname = "";
        logger.debug("getuname:" + uname);
        int pageno = 1;
        try {
            pageno = Integer.parseInt(request.getParameter("pageno"));
        } catch (Exception e) {
        }
        ArrayList<Consumer> lstform = consumerService.page(uname, pageno);
        logger.debug("page=======" + lstform.size());
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().println(lstform);
    }

    @RequestMapping(value = "/ajaxdel")
    String ajaxdel(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        int uid = Integer.parseInt(request.getParameter("uid"));
        if (consumerService.ajaxdel(uid)) {
            resp.getWriter().println("success");
        } else{
            resp.getWriter().println("fail");
        }
        return "consumerform";
    }

    @RequestMapping(value = "/update",method = RequestMethod.GET)
    String update(){
        return "modconsumer";
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    String update(HttpServletRequest request, HttpServletResponse response,
	@RequestParam Map map,MultipartFile picFile) throws ServletException, IOException {
        logger.debug(map);
        String sFile=request.getServletContext().getRealPath("/")+"/static/upimgs/"+map.get("uid")+"_.png";
        logger.debug(sFile);
        try {
            logger.debug(picFile);
            picFile.transferTo(new File(sFile));//文件保存至这个地方
        }catch (Exception e){
            logger.debug(e.getMessage());
        }
        if (consumerService.update(map)) {
            response.getWriter().println("success");
        } else{
            response.getWriter().println("fail");
        }
        return "redirect:/consumer";

    }

    @RequestMapping(value = "/insert" ,method = RequestMethod.GET)
    private String insert(){
        return "addconsumer";
    }

    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    String insert(HttpServletRequest request, HttpServletResponse response, Consumer consumer, MultipartFile picFile) throws ServletException, IOException {
        logger.debug(picFile);
        String sFile=request.getServletContext().getRealPath("/")+"/static/upimgs/"+consumer.getUid()+"_.png";
        logger.debug("图片文件"+sFile);
        try {
            picFile.transferTo(new File(sFile));//文件保存至这个地方
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        if (consumerService.register(consumer)) {
            response.getWriter().println("success");
            return "redirect:/consumer";
        } else {
            request.setAttribute("hint","insert failure");
            return "addconsumer";
        }

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    //地址栏请求consumer/login 会显示登陆界面
    private String login() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    String login(HttpServletRequest request, HttpServletResponse resp, Consumer consumer) throws ServletException, IOException {

        if (consumerService.login(consumer)) {  //登陆成功  在当前会话属性 loginStatus 设置为null
            request.getSession().setAttribute("loginStatus", consumer);
            logger.debug("登陆成功！！！！！！！！！！！！");
            return "home"; //  redirect重定向到8080下的路径
        } else {                         //登陆失败loginStatus设置为null
            request.getSession().setAttribute("loginStatus", null);
            logger.debug("登录失败！！！！！！！！！");
            request.setAttribute("hint", "无效的用户或密码");
            return "login";
        }
    }

//    @RequestMapping(value = "/home",method = RequestMethod.GET)
//    String home(){
//        return "home.jsp";
//    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    private String register() {
        return "register";
    }
    //地址栏请求consumer/register 会显示注册界面

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    private ModelAndView register( Consumer consumer) throws ServletException, IOException {
        logger.debug(consumer);
        if (consumerService.register(consumer)) {  //登陆成功  在当前会话属性 loginStatus 设置为null
            logger.debug("register success!");
            return new ModelAndView("login");//创建新视图
        } else {                         //登陆失败loginStatus设置为null
            logger.debug("register fail");
            return new ModelAndView("register", "hint", "注册失败");
        }
    }





    @RequestMapping(value = "/checkid/{uid}")
    void checkid(HttpServletRequest request, HttpServletResponse resp, Consumer consumer,
               @PathVariable int uid) throws IOException {
    logger.debug("uid"+uid);
        if ( consumerService.checkid(uid)) {
            resp.getWriter().print("exist");
            logger.debug("checkid========exist");
            System.out.println("存在====================");
        } else {
            resp.getWriter().print("not");
            System.out.println("不存在====================");
        }
}

    @RequestMapping(value = "/checktel/{utel}")
    void checktel(HttpServletRequest request, HttpServletResponse resp, Consumer consumer,
                 @PathVariable String utel) throws IOException {
        System.out.println(utel);
        if ( consumerService.checktel(utel)) {
            resp.getWriter().print("exist");
            logger.debug("checktel========exist");
            System.out.println("存在====================");
        } else {
            resp.getWriter().print("not");
            System.out.println("不存在====================");
        }
    }

}
