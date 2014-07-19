/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-server
 * 文件名：	LogPipelineFactoryBean.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-30 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;

import com.hd123.devops.logmonitor.pipeline.LogPipeline;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipeline;

/**
 * 流水线构造Bean
 * 
 * @author zhangyanbo
 * 
 */
public class LogPipelineFactoryBean implements FactoryBean<LogPipeline> {

  private String name;
  private List<NamedLogHandler> handlers = new ArrayList();

  @Override
  public LogPipeline getObject() throws Exception {
    LogPipeline pipeline = null;
    if (name != null)
      pipeline = new DefaultLogPipeline(name);
    else
      pipeline = new DefaultLogPipeline();
    for (NamedLogHandler handler : handlers)
      pipeline.addLast(handler.getName(), handler.getHandler());
    return pipeline;
  }

  @Override
  public Class<?> getObjectType() {
    return DefaultLogPipeline.class;
  }

  @Override
  public boolean isSingleton() {
    return false;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setHandlers(List<NamedLogHandler> handlers) {
    this.handlers = handlers;
  }
}
