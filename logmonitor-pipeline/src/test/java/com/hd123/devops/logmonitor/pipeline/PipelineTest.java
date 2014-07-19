/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	PipelineTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.devops.logmonitor.pipeline.handler.gateway.Condition;
import com.hd123.devops.logmonitor.pipeline.handler.gateway.Forker;
import com.hd123.devops.logmonitor.pipeline.handler.parser.CharTokenizer;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;

/**
 * @author zhangyanbo
 * 
 */
public class PipelineTest {
  private static final Logger logger = LoggerFactory.getLogger(PipelineTest.class);

  @Test
  public void addLast() throws Exception {
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline().addLast("a", new MyHandler("a"))
        .addLast("b", new MyHandler("b")).addLast("c", new MyHandler("c"));
    Assert.assertEquals(((MyHandler) pipeline.getFirst()).getName(), "a");
    Assert.assertEquals(((MyHandler) pipeline.getLast()).getName(), "c");

    logger.info("=========addLast========");
    pipeline.handleEvent(new DefaultLogEvent("test"));
  }

  @Test
  public void addFirst() throws Exception {
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline().addFirst("a", new MyHandler("a"))
        .addFirst("b", new MyHandler("b")).addFirst("c", new MyHandler("c"));
    Assert.assertEquals(((MyHandler) pipeline.getFirst()).getName(), "c");
    Assert.assertEquals(((MyHandler) pipeline.getLast()).getName(), "a");

    logger.info("=========addFirst========");
    pipeline.handleEvent(new DefaultLogEvent("test"));
  }

  @Test
  public void gateway() throws Exception {
    String line = "1212 abc 005";
    RawLog msg = new RawLog();
    msg.setBody(line.getBytes());
    String[] tokens = new String[] {
        "ta", "tb", "tc", "td" };
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline().addLast("a", new MyHandler("a"))
        .addLast("tokenizer", new CharTokenizer(tokens, ' '));

    LogPipeline fork1 = factory.getPipeline().addLast("x", new MyHandler("x"));

    LogPipeline fork2 = factory.getPipeline().addLast("cond", new Condition("tb", "a.*"))
        .addLast("b", new MyHandler("b"));

    LogPipeline fork3 = factory.getPipeline().addLast("cond2", new Condition("tb", "b.*"))
        .addLast("c", new MyHandler("c"));

    pipeline.addLast("fork", new Forker().addPipeline(fork1, fork2, fork3));

    logger.info("=========gateway========");
    pipeline.handleEvent(new DefaultLogEvent(msg));
  }

  static class MyHandler implements LogHandler {
    private static final Logger logger = LoggerFactory.getLogger(MyHandler.class);
    private String name;

    public MyHandler(String name) {
      this.name = name;
    }

    @Override
    public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
      logger.info(name + " invoked");

      ctx.invokeNext(e);
    }

    public String getName() {
      return name;
    }
  }
}
