package com.maidao.statistics.web.model.sms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 返回前端数据
 *
 *@author: Weiyf
 *@Date: 2018/10/17 10:36
 */
@Getter
@Setter
@Builder
public class SmsInfoResult {

    //模板编号
    private String templateCode;

    private String templateName;

    //时间
    private List<String> dateDayStr;

    //总发送量
    private List<Integer> totalSendNumber;
}
