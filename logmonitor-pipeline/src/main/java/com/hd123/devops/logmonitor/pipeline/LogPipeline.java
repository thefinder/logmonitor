/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	LogPipeline.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.util.Set;

/**
 * 日志处理流水线
 * 
 * @author zhangyanbo
 * 
 */
public interface LogPipeline {

  /**
   * 取得流水线名称。
   * 
   * @return 名称。
   */
  String getName();

  /**
   * 在流水线的开始插入一个处理器。
   * 
   * @param name
   *          处理器名，not null
   * @param handler
   *          处理器，not null
   * @return 流水线。
   */
  LogPipeline addFirst(String name, LogHandler handler);

  /**
   * 在流水线的最后追加一个处理器。
   * 
   * @param name
   *          处理器名，not null
   * @param handler
   *          处理器，not null
   * @return 流水线。
   */
  LogPipeline addLast(String name, LogHandler handler);

  /**
   * 从流水线上移除一个处理器。
   * 
   * @param handler
   *          处理器，not null
   */
  void remove(LogHandler handler);

  /**
   * 从流水线上移除指定名称的处理器。
   * 
   * @param name
   *          处理器名称，not null
   * @return 被移除的处理器，如果流水线无此处理器，返回null。
   */
  LogHandler remove(String name);

  /**
   * 从流水线上移除第一个处理器。
   * 
   * @return 被移除的处理器，如果流水线不包含任何处理器，返回null。
   */
  LogHandler removeFirst();

  /**
   * 从流水线上移除最后一个处理器。
   * 
   * @return 被移除的处理器，如果流水线不包含任何处理器，返回null。
   */
  LogHandler removeLast();

  /**
   * 取得流水线上的第一个处理器。
   * 
   * @return 处理器，不存在则返回null。
   */
  LogHandler getFirst();

  /**
   * 取得流水线上的最后一个处理器。
   * 
   * @return 处理器，不存在则返回null。
   */
  LogHandler getLast();

  /**
   * 取得指定名称的处理器。
   * 
   * @param name
   *          处理器名称，not null
   * @return 处理器，不存在则返回null。
   */
  LogHandler get(String name);

  /**
   * 取得流水线上所有已注册的处理器名称。
   * 
   * @return 名称列表，如果流水线不包含任何处理器返回空集合。
   */
  Set<String> getNames();

  /**
   * 将一个事件加入流水线处理。
   * 
   * @param e
   *          事件，not null
   * @throws Exception
   *           处理失败抛出异常。
   */
  void handleEvent(LogEvent e) throws Exception;
}
