package com.maidao.statistics.util;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 封装httpclinet
 * <p>
 * 注意事项  ！！！
 * 1.url必须以http or https开头
 * 2.异常直接抛出，需要在使用的地方自行处理异常
 *
 * @author: Weiyf
 * @Date: 2018/2/27 10:20
 */
public class OkhttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(OkhttpClientUtil.class);

    private static final MediaType Json = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
    
    private static final int Connect_Timeout_Ten_Seconds = 10;

    private static final int Read_Timeout_Thirty_Secouds = 30;

    private static OkHttpClient okHttpClient;

    static {
        if(okHttpClient == null ){
            okHttpClient = new OkHttpClient.Builder().connectTimeout(Connect_Timeout_Ten_Seconds, TimeUnit.SECONDS)
                .readTimeout(Read_Timeout_Thirty_Secouds, TimeUnit.SECONDS).build();
        }
    }

    /**
     * post请求
     **/
    public static String post(Request request) throws IOException {
        OkHttpClient client = okHttpClient;
        Response response = client.newCall(request).execute();

        //解析返回信息
        String result = analysisResult(response);

        return result;
    }

    /**
     * get请求
     **/
    public static String get(Request request) throws IOException {

        OkHttpClient client = okHttpClient;
        Response response = client.newCall(request).execute();

        //解析返回信息
        String result = analysisResult(response);

        return result;
    }

    /**
     * 解析返回结果
     **/
    private static String analysisResult(Response response) throws IOException {

        InputStream inputStream = null;
        ByteArrayOutputStream out = null;
        try {
            inputStream = response.body().byteStream();
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            byte[] ss = out.toByteArray();
            String result = new String(ss, "utf-8");
            return result;
        } catch (Exception e){
            return null;
        } finally {
            if(out != null){
                out.flush();
                out.close();
            }

            if(inputStream != null){
                inputStream.close();
            }

        }

    }

    /**
     * bean to FormBody
     */
    public static RequestBody beanToRequestBody(Object bean) {
        FormBody.Builder builder = new FormBody.Builder();
        Field[] fields = bean.getClass().getDeclaredFields();
        Arrays.asList(fields).forEach(f -> {
            try {
                f.setAccessible(true);
                builder.add(f.getName(), JSON.toJSONString(f.get(bean)));
            } catch (IllegalAccessException e) {
                logger.error("beanToRequestBody transfer failure ", e);
            }
        });

        return builder.build();
    }

    /**
     * JSON请求 RequestBody
     */
    public static RequestBody beanToJsonRequestBody(Object bean) {
        return RequestBody.create(Json, JSON.toJSONString(bean));
    }

}
