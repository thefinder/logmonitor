/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	DefaultLogHandlerContext.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.impl;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.LogPipeline;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author zhangyanbo
 * 
 */
public class DefaultLogHandlerContext implements LogHandlerContext {
  volatile DefaultLogPipeline pipeline;
  volatile DefaultLogHandlerContext prev;
  volatile DefaultLogHandlerContext next;
  private final String name;
  private final LogHandler handler;

  public DefaultLogHandlerContext(String name, LogHandler handler, DefaultLogPipeline pipeline,
      DefaultLogHandlerContext prev, DefaultLogHandlerContext next) {
    Assert.assertArgumentNotNull(name, "name");
    Assert.assertArgumentNotNull(handler, "handler");

    this.name = name;
    this.pipeline = pipeline;
    this.handler = handler;
    this.prev = prev;
    this.next = next;
  }

  @Override
  public LogHandler getHandler() {
    return handler;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public LogPipeline getPipeline() {
    return pipeline;
  }

  @Override
  public void invokeNext(LogEvent e) throws Exception {
    DefaultLogHandlerContext ctx = this.next;
    if (ctx != null)
      ctx.getHandler().invoke(ctx, e);
  }

  @Override
  public LogEvent buildEvent(LogEvent e) {
    if (e instanceof DefaultLogEvent == false)
      throw new UnsupportedOperationException("不支持 " + e.getClass().getName());

    return new DefaultLogEvent(e.getLog());
  }

  @Override
  public LogEvent buildEvent(LogEvent e, Object log) {
    if (e instanceof DefaultLogEvent == false)
      throw new UnsupportedOperationException("不支持 " + e.getClass().getName());

    if (log == null)
      return new DefaultLogEvent();
    else
      return new DefaultLogEvent(log);
  }
}
