package com.wyf.util.keywordsfilter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.wyf.util.keywordsfilter.dict.FilterDict.*;

/**
 * 敏感词查找、替换工具类
 *
 *@author: Weiyf
 *@Date: 2018/11/19 14:01
 */
public class KeyWordsFilterUtil {

    private static ConcurrentHashMap sensitiveWordMap;

    static {
        if(sensitiveWordMap == null) {
            sensitiveWordMap = new KeyWordsInit().initKeyWord();
        }
    }

    /**
     * 获取敏感词
     */
    public static Set<String> getSensitiveWord(String txt){
        txt = txt.replaceAll(RegStr,"");
        Set<String> sensitiveWordList = checkSensitiveWord(txt);

        return sensitiveWordList;
    }

    //原方法
    /*public Set<String> getOldWord(String txt){
        txt = txt.replaceAll(RegStr,"");
        Set<String> sensitiveWordList = checkWords(txt);

        return sensitiveWordList;
    }*/

    /**
     * 替换敏感词
     */
    public static String replaceSensitiveWord(String txt,String replaceChar){
        txt = txt.replaceAll(RegStr,"");
        String resultTxt = txt;
        Set<String> set = getSensitiveWord(txt);
        Iterator<String> iterator = set.iterator();
        String word;
        String replaceString;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    /**
     * 获取需要替换的字符
     */
    private static String getReplaceChars(String replaceChar,int length){
        String resultReplace = replaceChar;
        for(int i = 1 ; i < length ; i++){
            resultReplace += replaceChar;
        }

        return resultReplace;
    }

    /**
     * 检查文字中是否包含敏感字符，检查规则如下：
     *
     * 英文&数字开头：匹配度不高，待优化
     * 中文：敏感词词条长度为1时全词匹配，词条长度>1时全词or匹配2个字以上
     *
     * @param txt 待处理文本
     * @return 匹配到的敏感词
     */
    private static Set<String> checkSensitiveWord(String txt){
        //敏感词结束标识位：用于敏感词只有1位的情况
        Map newMap = null;

        //返回的敏感词条
        Set<String> result = new HashSet<>();

        //记录匹配到的词条
        String filterWords = "";
        int flag = 0;
        //从当前匹配到的位置开始循环
        for(int j=0; j<txt.length(); j++){
            char firstWord = txt.charAt(j);

            //首次循环跳过
            if(flag == 0){
                newMap = (Map) sensitiveWordMap.get(firstWord);
            } else if(newMap != null && flag != 0) {
                //从外层匹配到的词条中往下找
                newMap = (Map) newMap.get(firstWord);
            }

            //上一次匹配结束，记录合法的结果
            if(newMap == null){
                if(filterWords.length() > 1
                    || sensitiveWordMap.get(filterWords) != null){
                   result.add(filterWords);
                    //当前字符再检测一次
                    j = j - 1;
                }
                filterWords = "";
                flag = 0;

            } else {
                filterWords += firstWord;
                flag++;
            }
        }

        return result;
    }

    //原方法
    private Set<String> checkWords(String txt){

        //返回的敏感词条
        Set<String> result = new HashSet<>();

        for(int j=0; j<txt.length(); j++) {
            //敏感词结束标识位：用于敏感词只有1位的情况
            boolean flag = false;
            //匹配标识数默认为0
            String matchFlag = "";
            char word = 0;
            Map nowMap = sensitiveWordMap;

            for (int i = j; i < txt.length(); i++) {
                word = txt.charAt(i);
                nowMap = (Map) nowMap.get(word);

                //存在，则判断是否为最后一个
                if (nowMap != null) {
                    //找到相应key，累加
                    matchFlag += word;
                    //如果为最后一个匹配规则,结束循环，返回匹配标识数
                    if (OneStr.equals(nowMap.get(IsEnd))) {
                        //结束标志位为true
                        flag = true;
                    }
                } else {
                    //不存在，直接返回
                    j=i;
                    break;
                }
            }
            if (matchFlag.length() < 2 && !flag) {
                continue;
            }
            if(matchFlag.length() == 1
                && sensitiveWordMap.get(matchFlag) == null){
                continue;
            }

            result.add(matchFlag);
        }
        return result;
    }

}
