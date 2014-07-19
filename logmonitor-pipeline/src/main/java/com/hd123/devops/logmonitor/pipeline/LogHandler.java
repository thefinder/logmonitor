/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	LogHandler.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

/**
 * 日志处理器
 * 
 * @author zhangyanbo
 * 
 */
public interface LogHandler {

  /**
   * 处理日志。
   * 
   * @param ctx
   *          日志处理器上下文，not null
   * @param e
   *          日志事件，not null
   * @throws Exception
   *           处理失败抛出异常。
   */
  void invoke(LogHandlerContext ctx, LogEvent e) throws Exception;
}
