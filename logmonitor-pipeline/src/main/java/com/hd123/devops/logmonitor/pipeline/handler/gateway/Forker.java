/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	Forker.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.handler.gateway;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.LogPipeline;
import com.hd123.h5.ddps.schedule.RunOnceTask;
import com.hd123.h5.ddps.schedule.Scheduler;

/**
 * 并行处理器，可以同时启动多个Pipeline的执行。
 * 
 * @author zhangyanbo
 * 
 */
public class Forker implements LogHandler {
  private final Logger logger = LoggerFactory.getLogger(Forker.class);
  private List<LogPipeline> pipelines = new ArrayList();
  private Scheduler scheduler = null;
  private boolean asynchronized = true;
  private boolean debug = logger.isDebugEnabled();

  public Forker() {
  }

  public Forker(List<LogPipeline> pipelines) {
    for (LogPipeline p : pipelines)
      this.pipelines.add(p);
  }

  public Forker addPipeline(LogPipeline... pipelines) {
    for (LogPipeline p : pipelines)
      this.pipelines.add(p);
    return this;
  }

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    Scheduler sch = getScheduler();
    final LogEvent event = e;
    for (final LogPipeline pipeline : pipelines) {
      sch.sched(new RunOnceTask(null) {
        @Override
        public void run() {
          try {
            if (debug)
              logger.debug("启动Pipeline " + pipeline.getName());
            pipeline.handleEvent(event);
          } catch (Exception ex) {
            logger.error("", ex);
          }
        }
      });
    }

    // 同步则等待执行完成
    if (asynchronized == false) {
      while (sch.getTaskCount() > 0) {
        Thread.sleep(100);
      }
    }
  }

  private Scheduler getScheduler() {
    if (scheduler != null)
      return scheduler;

    synchronized (this) {
      scheduler = new Scheduler(pipelines.size());
      return scheduler;
    }
  }

  /** 是否异步执行，默认是同步执行 */
  public boolean isAsynchronized() {
    return asynchronized;
  }

  public void setAsynchronized(boolean asynchronized) {
    this.asynchronized = asynchronized;
  }

}
