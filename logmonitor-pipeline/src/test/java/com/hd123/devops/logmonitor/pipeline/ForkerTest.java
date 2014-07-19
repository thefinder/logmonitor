/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	ForkerTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-9 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import org.junit.Assert;
import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.gateway.Forker;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;

/**
 * @author zhangyanbo
 * 
 */
public class ForkerTest {

  @Test
  public void test() throws Exception {
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline();

    LogPipeline p1 = factory.getPipeline("p1");
    p1.addLast("sleep", new LogHandler() {
      @Override
      public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
        System.out.println("sleep 1s");
        Thread.sleep(1000);
      }
    });

    LogPipeline p2 = factory.getPipeline("p2");
    p2.addLast("sleep", new LogHandler() {
      @Override
      public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
        System.out.println("sleep 2s");
        Thread.sleep(2000);
      }
    });

    Forker forker = new Forker();
    forker.addPipeline(p1, p2);
    pipeline.addLast("forker", forker);

    // 异步
    forker.setAsynchronized(true);
    long start = System.currentTimeMillis();
    pipeline.handleEvent(new DefaultLogEvent("test"));
    long end = System.currentTimeMillis();
    Assert.assertTrue(end - start < 1000);

    // 同步
    forker.setAsynchronized(false);
    start = System.currentTimeMillis();
    pipeline.handleEvent(new DefaultLogEvent("test"));
    end = System.currentTimeMillis();
    Assert.assertTrue(end - start > 1000);
  }
}
