package com.my.statistics.web.service.aliSms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.my.statistics.constant.AliSmsConstant;
import com.my.statistics.util.LocalDateUtil;
import com.my.statistics.util.OkhttpClientUtil;
import com.my.statistics.web.model.Resp;
import com.my.statistics.web.model.sms.SmsInfoParam;
import com.my.statistics.web.model.sms.SmsInfoDto;
import com.my.statistics.web.model.sms.SmsInfoResult;
import com.my.statistics.web.model.sms.SmsTemplateResult;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class AliSmsService {

    private static CountDownLatch latch = new CountDownLatch(5);
    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            5,
            10,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(5));

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取请求信息详情
     *
     *@author: Weiyf
     *@Date: 2018/10/12 17:14
     */
    public Resp<List<SmsInfoResult>> findSendInfo(SmsInfoParam param){

        /** 模板编号**/
        Resp<List<SmsTemplateResult>> templateResult = findSmsTemplateInfo(param.getCookie());

        if(templateResult.isFailed() && templateResult.getData().size() == 0){
            return Resp.failed("获取模板信息失败");
        }
        List<SmsTemplateResult> templateList = templateResult.getData();

        //重新设置统计线程的数量
        latch = new CountDownLatch(templateList.size());

        /** 模板发放记录**/
        Map<String,List<SmsInfoDto>> listResult = findSmsInfoList(param,templateList);

        /** 组装返回结果**/
        List<SmsInfoResult> result = new ArrayList<>();
        for(int i=0; i<templateList.size(); i++){
            SmsTemplateResult tempInfo = templateList.get(i);

            //获取信息
            List<SmsInfoDto> infoList = listResult.get(tempInfo.getTemplateCode());
            if(CollectionUtils.isEmpty(infoList)){
                log.warn("[获取模板发送信息]===>获取失败，code={}",tempInfo.getTemplateCode());
                continue;
            }

            List<String> dateDayStr = new ArrayList<>();
            List<Integer> sendTotal = new ArrayList<>();
            for(SmsInfoDto dto : infoList){
                dateDayStr.add(dto.getDetailStsDate());
                sendTotal.add(dto.getSendTotal());
            }

            SmsInfoResult infoResult = SmsInfoResult.builder()
                    .templateCode(tempInfo.getTemplateCode())
                    .templateName(tempInfo.getTemplateName())
                    .dateDayStr(dateDayStr)
                    .totalSendNumber(sendTotal)
                    .build();

            result.add(infoResult);
        }

        return Resp.success(result);
    }


    private Map findSmsInfoList(SmsInfoParam param,List<SmsTemplateResult> templateList){

        Map<String,List<SmsInfoDto>> listResult = new HashMap();

        long startTime = LocalDateUtil.currentTimeSecond();
        poolExecutor.execute(() -> {
            for(int i=0; i<templateList.size(); i++){

                //组装请求参数
                String templateCode = templateList.get(i).getTemplateCode();
                RequestBody formBody = new FormBody.Builder()
                        .add("template", templateCode)
                        .add("startDate", LocalDateUtil.strToDate(param.getStartTime(), LocalDateUtil.DatePattern.LONG).toString())
                        .add("endDate", LocalDateUtil.strToDate(param.getEndTime(), LocalDateUtil.DatePattern.LONG).toString())
                        .add("_input_charset", AliSmsConstant.CharSet)
                        .add("sec_token", AliSmsConstant.SmsSecToken)
                        .build();

                String cookies = param.getCookie();
                Request request = new Request.Builder()
                        .url(AliSmsConstant.SmsSendUrl)
                        .post(formBody)
                        .headers(new Headers.Builder()
                                .add("Content-Type","application/x-www-form-urlencoded")
                                .add("cookie",cookies)
                                .build())
                        .build();
                String result = null;
                try{
                    result = OkhttpClientUtil.post(request);
                } catch (RuntimeException | IOException e){
                    log.warn("请求失败===》error={}",e.getMessage());
                    return;
                }

                String resultData = String.valueOf(JSON.parseObject(result).get("data"));
                String resultList = JSONObject.toJSONString(JSON.parseObject(resultData).get("data"));

                listResult.put(templateCode,JSONArray.parseArray(resultList, SmsInfoDto.class));
                latch.countDown();
            }
        });

        //所有线程执行完毕
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.warn("操作线程失败===》error={}",e.getMessage());
        }

        long endTime = LocalDateUtil.currentTimeSecond();
        log.info("[执行耗时]===》second={}",endTime - startTime);

        return listResult;
    }

    /** 请求短信模板**/
    private Resp<List<SmsTemplateResult>> findSmsTemplateInfo(String cookie){

        //组装请求参数
        RequestBody formBody = new FormBody.Builder()
                .add("pageSize", "99")
                .add("pageNo","1")
                .add("_input_charset", AliSmsConstant.CharSet)
                .add("bizType",AliSmsConstant.BizType)
                .add("sec_token", AliSmsConstant.SmsSecToken)
                .build();

        Request request = new Request.Builder()
                .url(AliSmsConstant.SmsTemplateUrl)
                .post(formBody)
                .headers(new Headers.Builder()
                        .add("Content-Type","application/json;charset=UTF-8")
                        .add("cookie",cookie)
                        .build())
                .build();
        String result = null;
        try{
            result = OkhttpClientUtil.post(request);
        } catch (RuntimeException | IOException e){
            log.warn("[请求]===>errror={}",e.getMessage());
            return Resp.failed("请求失败");
        }
        log.info("[请求结果]====>info={}",JSONObject.toJSONString(result));
        String resultData = JSON.parseObject(String.valueOf(JSON.parseObject(result).get("data"))).get("data").toString();
        List<SmsTemplateResult> listResult = JSONArray.parseArray(resultData,SmsTemplateResult.class);

        return Resp.success(listResult);
    }

}
