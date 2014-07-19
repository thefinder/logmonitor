/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	PatternFilter.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-9 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author zhangyanbo
 * 
 */
public class PatternFilter implements LogFilter {
  private List<Pattern> regexes = new ArrayList();

  @Override
  public boolean accept(String line) {
    for (Pattern p : regexes)
      if (p.matcher(line).matches())
        return true;
    return false;
  }

  public void addPattern(String pattern) {
    this.regexes.add(Pattern.compile(pattern));
  }

  public String toString() {
    return "匹配模式：" + regexes.toString();
  }
}
