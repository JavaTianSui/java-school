package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        page.setPath("/index");
        page.setRows(discussPostService.findDiscussPostRows(0));
        //要关联用户，封装帖子和user信息
        List<Map<String,Object>> list = new ArrayList<>();
        //非空判断
        if (discussPosts != null){
            //遍历discussposts
            for (DiscussPost discussPost:discussPosts){
                //新建一个map
                Map<String, Object> map = new HashMap<>();
                //把discussPost(帖子)对象加进去
                map.put("discussPost",discussPost);
                //根据userID查user
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user",user);
                list.add(map);
            }
        }

        model.addAttribute("discussPosts",list);
        return "/index";

    }


}
