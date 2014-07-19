/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	Log.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-15 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.server;

/**
 * @author zhangyanbo
 * 
 */
public class Command {
  public static final String CMD_LOG = "LOG";
  public static final String CMD_PING = "PING";

  private String cmd;
  private String context;
  private byte[] body;

  public Command() {
  }

  public Command(String cmd, String context, byte[] body) {
    this.cmd = cmd;
    this.context = context;
    this.body = body;
  }

  public String getCmd() {
    return cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
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
