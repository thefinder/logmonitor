/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	LogMessage.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

/**
 * 日志事件
 * 
 * @author zhangyanbo
 * 
 */
public interface LogEvent {

  /**
   * 取得事件包含的日志。
   * 
   * @return 消息。
   * 
   */
  Object getLog();

}
