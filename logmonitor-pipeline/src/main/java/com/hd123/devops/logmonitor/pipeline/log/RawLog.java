/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	RawLog.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.log;

/**
 * 日志原生消息
 * 
 * @author zhangyanbo
 * 
 */
public class RawLog {

  private String context;
  private byte[] body;

  public RawLog() {
  }

  public RawLog(RawLog source) {
    this.context = source.getContext();
    this.body = source.getBody();
  }
  
  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public byte[] getBody() {
    return body;
  }

  public void setBody(byte[] body) {
    this.body = body;
  }
}
