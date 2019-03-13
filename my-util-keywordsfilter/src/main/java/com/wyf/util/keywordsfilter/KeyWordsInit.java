package com.wyf.util.keywordsfilter;



import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.wyf.util.keywordsfilter.dict.FilterDict.*;

/**
 * 关键词格式转换
 * DFA算法
 *@author: Weiyf
 *@Date: 2018/11/19 10:12
 */
public class KeyWordsInit {

    /** 当前项目中文件路径**/
    private static final String filePath = ResourceUtils.CLASSPATH_URL_PREFIX + "static/keywords.txt";

    /** 其他项目中引用时jar包中文件位置**/
    public static final String jarFilePath = "/static/keywords.txt";


    public static ConcurrentHashMap sensitiveWordMap;

    public KeyWordsInit(){
        super();
    }

    public ConcurrentHashMap initKeyWord(){
        try {
            Set<String> keyWordSet = convertKeyWords();
            addSensitiveWordToHashMap(keyWordSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }


    /**
     * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
     * 中 = {
     *      isEnd = 0
     *      国 = {
     *           isEnd = 1
     *           人 = {isEnd = 0
     *                民 = {isEnd = 1}
     *                }
     *           男  = {
     *                  isEnd = 0
     *                   人 = {
     *                        isEnd = 1
     *                       }
     *               }
     *           }
     *      }
     *  五 = {
     *      isEnd = 0
     *      星 = {
     *          isEnd = 0
     *          红 = {
     *              isEnd = 0
     *              旗 = {
     *                   isEnd = 1
     *                  }
     *              }
     *          }
     *      }
     */
    private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
        //初始化敏感词容器，减少扩容操作
        sensitiveWordMap = new ConcurrentHashMap<String,String>(keyWordSet.size());
        String key;
        Map nowMap;
        Map<String, String> newWorMap;
        Iterator<String> iterator = keyWordSet.iterator();
        while(iterator.hasNext()){
            key = iterator.next();
            nowMap = sensitiveWordMap;
            for(int i = 0 ; i < key.length() ; i++){
                char keyChar = key.charAt(i);
                Object wordMap = nowMap.get(keyChar);

                //如果存在该key，直接赋值
                if(wordMap != null){
                    nowMap = (Map) wordMap;
                }else{
                    newWorMap = new HashMap<>();
                    newWorMap.put(IsEnd, ZeroStr);
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if(i == key.length() - 1){
                    nowMap.put(IsEnd, OneStr);
                }
            }
        }
    }


    private Set<String> convertKeyWords() throws IOException {
        Set<String> set = null;


        InputStreamReader read = null;
        try {

//            read = getFile();
            read = getJarFile();

            set = new HashSet<>();
            BufferedReader bufferedReader = new BufferedReader(read);
            String txt;
            while((txt = bufferedReader.readLine()) != null){
                set.add(txt);
            }
        } catch (Exception e) {
            throw e;
        }finally{
            if(read != null){
                read.close();
            }
        }

        return set;
    }

    /**在当前项目中获取文件 **/
    private InputStreamReader getFile() throws FileNotFoundException {
        File file = ResourceUtils.getFile(filePath);
        if(file.isFile() && file.exists()){
            return new InputStreamReader(new FileInputStream(file));
        }
        return null;
    }

    /** 在jar包获取静态资源文件/获取编译后目录中的静态资源文件**/
    private InputStreamReader getJarFile(){
        return new InputStreamReader(this.getClass().getResourceAsStream(jarFilePath));
    }

}
