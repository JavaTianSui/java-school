package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        //类加载器，从target目录下
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyWord;
            while ((keyWord = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyWord(keyWord);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //将敏感词添加到前缀树中
    private void addKeyWord(String keyWord) {
        TrieNode tempNode = rootNode;
        for (int i = 0;i < keyWord.length();i++){
            char c = keyWord.charAt(i);
            TrieNode subNodes = tempNode.getSubNodes(c);
            if (subNodes == null){
                //初始化子节点
                subNodes = new TrieNode();
                tempNode.addSubNodes(c,subNodes);
            }

            //指针指向子节点，进入下一轮循环
            tempNode = subNodes;
            //设置结束标识
            if (i == keyWord.length() - 1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * create by: tiansui
     * create time:
     *过滤敏感词，
     * @return 过滤后的敏感词
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        //指针1--指向树
        TrieNode tempNode = rootNode;
        //指针2，3指向首位
        int begin = 0;
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()){
            char c = text.charAt(position);

            //跳过符号
            if (isSymbol(c)){
                //若指针1处于根节点，将此结果计入结果，让指针2向下走一步
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNodes(c);
            if (tempNode == null){
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            }else if (tempNode.isKeyWordEnd()){
                //发现敏感词,将begin-position字符串替换掉
                sb.append(REPLACEMENT);
                begin = ++position;
                //指向根节点
                tempNode = rootNode;
            }else {
                //检查下一个字符
                position++;
            }

        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0X9FFF);
    }

    //定义前缀树
    private class TrieNode{

        //关键词结束值标识
        private boolean isKeyWordEnd = false;

        //子节点(key是下级节点字符)
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNodes(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNodes(Character c){
            TrieNode trieNode = subNodes.get(c);
            return trieNode;
        }

    }

}
