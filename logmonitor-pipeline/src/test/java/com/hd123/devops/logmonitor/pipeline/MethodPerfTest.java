/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	PerfLogTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-27 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.analyzer.MethodPerfCounter;
import com.hd123.devops.logmonitor.pipeline.handler.parser.FileParser;
import com.hd123.devops.logmonitor.pipeline.handler.parser.Log4jParser;
import com.hd123.devops.logmonitor.pipeline.handler.parser.MethodPerfLogParser;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.PerfLog;

/**
 * @author zhangyanbo
 * 
 */
public class MethodPerfTest {

  @Test
  public void counter() throws Exception {
    String filename = "d:/work/perf/2014079/perflog#soms#192.168.32.227.log";
    String[] methods = new String[] {
        "*com.hd123.h5.soms.impl.order.OrderServiceImpl.saveNew()*",
        "*com.hd123.h5.soms.impl.order.OrderServiceImpl.prepare()*",
        "*com.hd123.h5.soms.impl.order.OrderServiceImpl.approve()*",
        "*com.hd123.h5.soms.impl.order.task.OrderNotifyWrhTask.access$000()/notifyWrhOrder()",
        "*com.hd123.h5.soms.impl.order.OrderServiceImpl.notifyWrh()*",
        "*com.hd123.h5.soms.impl.order.OrderServiceImpl.ship()*",
        "*com.hd123.h5.soms.impl.support.message.NotifierImpl.send()*",
        "*com.hd123.h5.soms.impl.support.search.OrderIndexingListener.onMessage()*" };
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory
        .getPipeline()
        .addLast("filecutter", new FileParser("[0-9]{4}-[0-9]{2}-[0-9]{2}.*"))
        .addLast("log4jtokenizer",
            new Log4jParser("%d [%t] %p %c - %m%n").withTokenMapper("%d", "date", "%m", "message"))
        .addLast("perftokenizer", new MethodPerfLogParser(methods))
        .addLast("perfcounter", new MethodPerfCounter());
    pipeline.handleEvent(new DefaultLogEvent(filename));
    print(pipeline);
  }

  public void print(LogPipeline pipeline) {
    MethodPerfCounter handler = (MethodPerfCounter) pipeline.get("perfcounter");
    List<PerfLog> list = new ArrayList(handler.getResult());
    Collections.sort(list, new Comparator<PerfLog>() {
      @Override
      public int compare(PerfLog o1, PerfLog o2) {
        int c = ObjectUtils.compare(o1.getWhat(), o2.getWhat());
        if (c != 0)
          return c;
        return ObjectUtils.compare(o1.getWhen(), o2.getWhen());
      }
    });

    for (PerfLog c : list) {
      System.out.println(MessageFormat.format("{0}, {1}: total={2}, avg={3}, max={4}, count={5}",
          c.getWhen(), c.getWhat(), c.getTotal(), c.getAvg(), c.getMax(), c.getCount()));
    }
  }
}
