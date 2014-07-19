/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	MethodPerfLogParserTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-9 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.parser.MethodPerfLogParser;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.PerfLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * @author zhangyanbo
 * 
 */
public class MethodPerfLogParserTest {

  @Test
  public void test() throws Exception {
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline();

    final List<PerfLog> logs = new ArrayList();
    String[] methods = new String[] {
      "*com.hd123.h5.soms.impl.order.OrderServiceImpl.saveNew()" };
    MethodPerfLogParser parser = new MethodPerfLogParser(methods);
    pipeline.addLast("parser", parser);
    pipeline.addLast("test", new LogHandler() {
      @Override
      public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
        if (e.getLog() instanceof PerfLog) {
          logs.add((PerfLog) e.getLog());
        }
      }
    });

    String message = "com.hd123.perf.log.PerfLogUtil - \r\ncom.hd123.h5.soms.impl.order.OrderServiceImpl.saveNew(): time=1000, count=1";
    TokenLog log = new TokenLog();
    log.setValue("message", message);
    log.setValue("date", "2014-07-09 02:11:19,593");
    pipeline.handleEvent(new DefaultLogEvent(log));
    Assert.assertEquals(logs.size(), 1);
    Assert.assertEquals(logs.get(0).getWhen(), "2014-07-09 02:11:19,593");
    Assert.assertEquals(logs.get(0).getWhat(),
        "com.hd123.h5.soms.impl.order.OrderServiceImpl.saveNew()");

    logs.clear();
    message = "com.hd123.perf.log.PerfLogUtil - \r\ncom.hd123.h5.soms.impl.order.OrderServiceImpl.save()/saveNew(): time=1000, count=1";
    log = new TokenLog();
    log.setValue("message", message);
    log.setValue("date", "2014-07-09 02:11:19,593");
    parser.setAggByPath(true);
    pipeline.handleEvent(new DefaultLogEvent(log));
    Assert.assertEquals(logs.size(), 1);
    Assert.assertEquals(logs.get(0).getWhen(), "2014-07-09 02:11:19,593");
    Assert.assertEquals(logs.get(0).getWhat(),
        "com.hd123.h5.soms.impl.order.OrderServiceImpl.save()/com.hd123.h5.soms.impl.order.OrderServiceImpl.saveNew()");
  }
}
