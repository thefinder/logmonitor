/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	LogHandlerContext.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

/**
 * 日志处理上下文
 * 
 * @author zhangyanbo
 * 
 */
public interface LogHandlerContext {

  /**
   * 取得日志处理器。
   * 
   * @return 日志处理器。
   */
  LogHandler getHandler();

  /**
   * 取得日志处理器名称。
   * 
   * @return 名称。
   */
  String getName();

  /**
   * 取得流水线。
   * 
   * @return 流水线。
   */
  LogPipeline getPipeline();

  /**
   * 移动到流水线下一个处理器执行。
   * 
   * @param e
   *          日志事件，not null
   * @throws Exception
   *           处理失败时抛出。
   */
  void invokeNext(LogEvent e) throws Exception;

  /**
   * 构造事件。
   * 
   * @param e
   *          原事件
   * @return 新事件。
   */
  LogEvent buildEvent(LogEvent e);

  /**
   * 构造事件，使用新的消息替代。
   * 
   * @param e
   *          原事件，not null
   * @param log
   *          日志对象
   * @return 新事件。
   */
  LogEvent buildEvent(LogEvent e, Object log);

}
