/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	DefaultLogHandlerRegistry.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.devops.logmonitor.pipeline.LogHandlerRegistry;

/**
 * @author zhangyanbo
 * 
 */
public class DefaultLogHandlerRegistry implements LogHandlerRegistry {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private Map<String, Class> handlers = new HashMap();

  @Override
  public void register(String alias, String handlerClass) throws Exception {
    if (handlers.containsKey(alias))
      throw new Exception("重复注册处理器：" + alias);

    try {
      handlers.put(alias, Class.forName(handlerClass));
    } catch (ClassNotFoundException e) {
      logger.error("找不到处理器类：" + handlerClass);
    }
  }

  @Override
  public void unregister(String alias, String handlerClass) {
    handlers.remove(alias);
  }

}
