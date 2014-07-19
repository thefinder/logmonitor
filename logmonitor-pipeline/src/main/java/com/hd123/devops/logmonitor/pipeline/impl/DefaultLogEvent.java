/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	DefaultLogEvent.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.impl;

import com.hd123.devops.logmonitor.pipeline.LogEvent;

/**
 * @author zhangyanbo
 * 
 */
public class DefaultLogEvent implements LogEvent {
  private Object log;

  public DefaultLogEvent() {
  }

  public DefaultLogEvent(Object log) {
    this.log = log;
  }

  public Object getLog() {
    return log;
  }

  public void setLog(Object log) {
    this.log = log;
  }

}
