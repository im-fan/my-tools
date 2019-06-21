package my.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import my.module.UserDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * lambda测试
 *
 *@author: Weiyf
 *@Date: 2019-06-21 10:34
 */
public class LambdaService {

    private  <E> void indexForEach( Iterable<? extends E> elements, BiConsumer<Integer, ? super E> action) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(action);
        int index = 0;
        for (E element : elements) {
            action.accept(index++, element);
        }
    }

    @Test
    public void indexTest(){

        buildDto().forEach(po -> System.out.println(JSONObject.toJSONString(po)));

        indexForEach(buildDto(), (index, order)
                -> System.out.println(index + " -> " + JSONObject.toJSONString(order)));

    }

    /**
     * SerializerFeature 是fastjson格式化时的配置
     *
     *@author: Weiyf
     *@Date: 2019-06-21 11:52
     */
    @Test
    public void copyTest(){
        List<UserDto> result = buildDto();
        result.add(UserDto.builder()
                .id(4)
                .userName("40001")
                .address(null)
                .build());

        System.out.println(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    private List<UserDto> buildDto(){

        List result = new ArrayList();

        result.add(UserDto.builder()
                .id(2)
                .userName("小张")
                .address("xx路2002号")
                .build());
        result.add(UserDto.builder()
                .id(3)
                .userName("小王")
                .address("xx路3003号")
                .build()
                );
        result.add(UserDto.builder()
                .id(1)
                .userName("小明")
                .address("xx路1001号")
                .build());

        return result;
    }



}
