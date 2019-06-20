## 关键词过滤
> DFA算法实现，参考文档 https://yq.aliyun.com/articles/69930?spm=5176.10695662.1996646101.searchclickresult.50185cdbtCd13G

### 使用说明
```text
1.引入jar包
<dependency>
    <groupId>com.wyf</groupId>
    <artifactId>sensitive-words-util</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

2.使用
获取敏感词:KeyWordsFilterUtil.getSensitiveWord(待检测字符串);
替换敏感词:KeyWordsFilterUtil.replaceSensitiveWord(待检测字符串，替换的字符);

3.词库
/resources/static/keywords.txt

4.注意点
    最左匹配原则!!!
        例：
        词库：你是个傻子
        文本：你/是个/是个傻/是个傻子[×] 你是/你是个/你是个傻/你是个傻子[√]
    匹配中文规则：
        全匹配/匹配到的字符>=2个词库中的字符
        例：词库：你是个傻子  文本：你是/你是个/你是个傻/你是个傻子[√]
    匹配字母/数字:
        误判率较高，未实现,后期准备做字母/数字全词匹配
  
```

