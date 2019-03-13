package com.maidao.statistics.web.controller;

import com.maidao.statistics.web.model.Resp;
import com.maidao.statistics.web.model.sms.SmsInfoParam;
import com.maidao.statistics.web.model.sms.SmsInfoResult;
import com.maidao.statistics.web.service.aliSms.AliSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 统计阿里云短信发送量
 *
 *@author: Weiyf
 *@Date: 2018/10/10 18:20
 */
@Controller
@RequestMapping("ali/sms")
public class AliSmsController {


    @Autowired
    AliSmsService aliSmsService;

    @GetMapping
    public ModelAndView index(){
        return new ModelAndView("index");
    }

    @RequestMapping( value = "/info",method = RequestMethod.POST)
    @ResponseBody
    public Resp<List<SmsInfoResult>> findSendInfo(@RequestBody SmsInfoParam param){
        Resp<List<SmsInfoResult>> result = aliSmsService.findSendInfo(param);

        return result;
    }

}
