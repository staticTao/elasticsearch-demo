package com.example.esjd.controller;

import com.example.esjd.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description:TODO
 * @Author: TGP
 * @Date: 2022/5/30 10:43
 **/

@RestController
public class ContentController {
    @Autowired
    private ContentService service;
    @GetMapping("/parse/{keyword}")
    Boolean parse(@PathVariable("keyword") String keyword) throws IOException {
        return service.parseContent(keyword);
    }
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    List<Map<String,Object>> search(@PathVariable("keyword")String keyword,
                                    @PathVariable("pageNo")int pageNo,
                                    @PathVariable("pageSize")int pageSize) throws IOException {
        return service.search(keyword,pageNo,pageSize);
    }
    @GetMapping("/highLightSearch/{keyword}/{pageNo}/{pageSize}")
    List<Map<String,Object>> highLightSearch(@PathVariable("keyword")String keyword,
                                    @PathVariable("pageNo")int pageNo,
                                    @PathVariable("pageSize")int pageSize) throws IOException {
        return service.highLightSearch(keyword,pageNo,pageSize);
    }
}
