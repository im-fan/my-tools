package com.my.statistics.web.model.sms;

import lombok.Getter;
import lombok.Setter;

/**
 * 短信模板内容
 *
 *@author: Weiyf
 *@Date: 2018/10/17 9:14
 */
@Getter
@Setter
public class SmsTemplateResult {

    private String  bizType;

    /** 短信类型 1-验证码 2-通知**/
    private Integer signType;

    //模板编号
    private String templateCode;

    //模板内容
    private String templateContent;

    //模板名称
    private String templateName;

}
