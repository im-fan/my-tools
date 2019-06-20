package com.my.statistics.web.model.sms;

import lombok.Getter;
import lombok.Setter;

/**
 * 请求短信平台返回结果
 *
 *@author: Weiyf
 *@Date: 2018/10/12 16:46
 */
@Getter
@Setter
public class SmsInfoDto {

    //发送失败总量
    private String sendTotalFail;

    //模板名称
    private String templateName;

    //总发送量
    private Integer sendTotal;

    //发送统计时间
    private String detailStsDate;

    //发送成功占比
    private String sendSuccessRate;

    //短信模板编码
    private String templateCode;

    //发送总成功数量
    private Integer sendTotalSuccess;

    //创建时间
    private String gmtCreate;

    //修改时间
    private String gmtModified;


}
