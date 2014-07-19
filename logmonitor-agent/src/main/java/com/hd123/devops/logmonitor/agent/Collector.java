/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	Collector.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-9 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

import java.util.regex.Pattern;

import com.hd123.devops.logmonitor.agent.filter.LogFilter;

/**
 * 日志收集器
 * 
 * @author zhangyanbo
 * 
 */
public class Collector {

  private String id;
  private LogFilter filter;
  private FileLocator file = new FileLocator();
  private Pattern newLinePattern;
  private String logContext = "";

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LogFilter getFilter() {
    return filter;
  }

  public void setFilter(LogFilter filter) {
    this.filter = filter;
  }

  public Pattern getNewLinePattern() {
    return newLinePattern;
  }

  public void setNewLinePattern(Pattern newLinePattern) {
    this.newLinePattern = newLinePattern;
  }

  public boolean isDetailLog(String s) {
    return s != null && newLinePattern != null && (newLinePattern.matcher(s).matches() == false);
  }

  public String getLogContext() {
    return logContext;
  }

  public void setLogContext(String logContext) {
    this.logContext = logContext;
  }

  public FileLocator getFile() {
    return file;
  }

  public void setFile(FileLocator file) {
    this.file = file;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("id = ").append(id).append(", ");
    sb.append("logcontext = ").append(logContext).append(", ");
    sb.append("file = ").append(file.getFileName()).append(", ");
    sb.append("filter = ").append(filter.getClass().getName());
    return sb.toString();
  }

}
