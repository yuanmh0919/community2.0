package com.yuanmh.community.utils;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yuanmh
 * @Date: 下午4:10 2024/6/21
 * @Describe: 敏感词过滤器
 */

@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    //敏感词替换符
    private static final String REPLACE_CHAR = "***";

    //初始化根节点
    private TrieNode rootNode = new TrieNode();


    //定义前缀树
    private class TrieNode {
        //敏感词借宿标识
        private boolean isKeyWordEnd = false;

        //子节点 key是下级字符，value是下级节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();


        /**
         * 添加子节点
         *
         * @param c       下级字符
         * @param subNode 下级节点
         */
        public void addSubNode(Character c, TrieNode subNode) {
            subNodes.put(c, subNode);
        }

        /**
         * 获取子节点
         *
         * @param c 下级字符
         * @return 下级节点
         */
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }
    }

    //根据敏感词 初始化前缀树
    //PostConstruct注解 当容器实例化这个Bean 调用这个类的构造器之后 这个方法会被自动调用
    @PostConstruct
    public void init() {
        //从类加载器获取敏感词文件
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //将字节流转换成字符流 并用BufferedReader包装
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        ) {
            //存储读取的字符
            String keyWord;
            while ((keyWord = bufferedReader.readLine()) != null) {
                //将读取的字符添加到前缀树中
                this.addKeyWord(keyWord);
            }
        } catch (IOException e) {
            LOGGER.error("加载敏感词文件失败: ", e.getMessage());
            throw new RuntimeException("加载敏感词文件失败！" + e);
        }

    }

    /**
     * 将敏感词添加到前缀树中
     *
     * @param keyWord
     */
    private void addKeyWord(String keyWord) {
        TrieNode tempNode = rootNode;
        //遍历敏感词
        for (int i = 0; i < keyWord.length(); i++) {
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                //如果不存在该字符的子节点，则创建该字符的子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            //指向子节点，进入下一轮循环
            tempNode = subNode;

            //设置结束标识
            if (i == keyWord.length() - 1) {
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    //编写过滤敏感词的方法

    /**
     * 过滤敏感词
     *
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //需要三个指针，分别指向待过滤文本的开始位置，当前指针，以及敏感词指针
        //指针1 指向根节点
        TrieNode tempNode = rootNode;
        //指针2 指向敏感词开始位置
        int begin = 0;
        //指针3 指向当前位置
        int position = 0;
        //结果
        StringBuilder result = new StringBuilder();
        //使用指针3 遍历待过滤文本
        while (position < text.length()) {
            char c = text.charAt(position);
            //忽略特殊字符
            if (isSymbol(c)) {
                //若指针1处于根节点 ，将此符号计入结果，让指针2指向下一个节点
                if (tempNode == rootNode) {
                    result.append(c);
                    begin = position++;
                }
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                //begin指向的不是敏感词
                result.append(text.charAt(begin));
                begin = begin + 1;
                position = begin;
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
                //发现敏感词
                result.append(REPLACE_CHAR);
                begin = ++position;
                tempNode = rootNode;
            } else {
                //指针2指向下一个字符
                position++;
            }
        }
        return result.toString();
    }


    /**
     * 判断是否是特殊符号
     * 并且c在东亚符号范围内 0x2E80~0x9FFF
     *
     * @param c 字符
     * @return 是特殊符号返回true 否则返回false
     */
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
        //CharUtils.isAsciiAlphanumeric(c)  判断是否是正常字符 如果是正常字符 返回true 否则返回false
    }
}
