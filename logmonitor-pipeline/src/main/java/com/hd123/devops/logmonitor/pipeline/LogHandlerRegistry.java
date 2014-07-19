/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	HandlerRepository.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

/**
 * 处理器注册表
 * 
 * @author zhangyanbo
 * 
 */
public interface LogHandlerRegistry {

  /**
   * 注册处理器。
   * 
   * @param type
   *          处理器别名，not null
   * @param handlerClass
   *          处理器类名，not null
   * @throws Exception
   *           注册失败时抛出。
   */
  void register(String alias, String handlerClass) throws Exception;

  /**
   * 取消注册处理器。
   * 
   * @param type
   *          处理器别名，not null
   * @param handlerClass
   *          处理器类名，not null
   */
  void unregister(String alias, String handlerClass);
}
