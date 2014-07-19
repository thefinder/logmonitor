/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	SimpleFilter.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-10 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyanbo
 * 
 */
public class WordFilter implements LogFilter {
  private List<String> words = new ArrayList();

  public void addWord(String word) {
    if (word != null && words.contains(word) == false)
      this.words.add(word);
  }

  @Override
  public boolean accept(String line) throws Exception {
    if (line == null)
      return false;

    for (String word : words)
      if (line.contains(word))
        return true;
    return false;
  }

  public String toString() {
    return "包含关键词: " + words.toArray();
  }
}
