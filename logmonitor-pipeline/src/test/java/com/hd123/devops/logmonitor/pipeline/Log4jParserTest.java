/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	Log4jParserTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-9 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.parser.Log4jParser;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * @author zhangyanbo
 * 
 */
public class Log4jParserTest {

  @Test
  public void test() throws Exception {
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline();
    final List<TokenLog> logs = new ArrayList();
    pipeline.addLast("parser", new Log4jParser("%d [%t] %p %c - %m%n"));
    pipeline.addLast("test", new LogHandler() {
      @Override
      public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
        if (e.getLog() instanceof TokenLog) {
          logs.add((TokenLog) e.getLog());
        }
      }
    });

    String s = "2014-05-21 08:40:05,904 [localhost-startStop-1] INFO org.springframework.web.context.ContextLoader - Root WebApplicationContext: initialization started";
    RawLog log = new RawLog();
    log.setBody(s.getBytes());
    pipeline.handleEvent(new DefaultLogEvent(log));
    Assert.assertEquals(logs.size(), 1);
    TokenLog t = logs.get(0);
    Assert.assertEquals(t.getValue("%d"), "2014-05-21 08:40:05");
    Assert.assertEquals(t.getValue("%t"), "localhost-startStop-1");
    Assert.assertEquals(t.getValue("%p"), "INFO");
    Assert.assertEquals(t.getValue("%c"), "org.springframework.web.context.ContextLoader");
    Assert.assertEquals(t.getValue("%m"), "Root WebApplicationContext: initialization started");
  }
}
