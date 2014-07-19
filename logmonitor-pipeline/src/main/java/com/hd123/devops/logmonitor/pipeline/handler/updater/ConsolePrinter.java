/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	ConsolePrinter.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-1 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.handler.updater;

import java.text.MessageFormat;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.log.PerfLog;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * 日志打印到控制台
 * 
 * @author zhangyanbo
 * 
 */
public class ConsolePrinter implements LogHandler {

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    if (e.getLog() instanceof PerfLog) {
      PerfLog c = (PerfLog) e.getLog();
      System.out.println(MessageFormat.format("{0}, {1}: total={2}, avg={3}, max={4}, count={5}",
          c.getWhen(), c.getWhat(), c.getTotal(), c.getAvg(), c.getMax(), c.getCount()));
    } else if (e.getLog() instanceof TokenLog) {
      TokenLog log = (TokenLog) e.getLog();
      System.out.println(log.getValues());
    } else if (e.getLog() instanceof RawLog) {
      RawLog log = (RawLog) e.getLog();
      System.out.print(log.getContext());
      if (log.getBody() != null)
        System.out.println(" " + new String(log.getBody(), "utf-8"));
    }

    ctx.invokeNext(e);
  }

}
