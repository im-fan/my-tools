package com.my.statistics.web.model.sms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsInfoParam {

    //统计开始时间
    private String startTime;

    //统计结束时间
    private String endTime;

    //用户cookie
    private String cookie;

}
