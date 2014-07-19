/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	WmsPerfTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-3 - zhangyanbo - 创建。
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
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.PerfLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * @author zhangyanbo
 * 
 */
public class WmsPerfTest {

  @Test
  public void analyze() throws Exception {
    String filename = "d:/work/perf/20140710/perf#h5#192.168.32.221.log";
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory
        .getPipeline()
        .addLast("filecutter",
            new FileParser("[0-9]{4}-[0-9]{2}-[0-9]{2}.*").withFileEncoding("GBK"))
        .addLast("log4jtokenizer", new Log4jParser("%d %p [%c] [%t] %m%n"))
        .addLast("wmsperf", new LogHandler() {
          @Override
          public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
            if (e.getLog() instanceof TokenLog == false)
              return;
            TokenLog log = (TokenLog) e.getLog();
            String dateStr = log.getValue("%d");
            String message = log.getValue("%m");
            if (message == null || message.contains("销售订单") == false)
              return;
            int beginIndex = message.indexOf("##TimedLog##-**");
            if (beginIndex < 0)
              return;
            int endIndex = message.indexOf("**-耗时", beginIndex);
            if (endIndex < 0)
              return;
            int perfIndex = message.indexOf("ms", endIndex);
            if (perfIndex < 0)
              return;
            String what = message.substring(beginIndex + "##TimedLog##-**".length(), endIndex)
                .trim();
            int index = what.indexOf(":");
            if (index > 0)
              what = what.substring(0, index) + ":?";
            long perf = Long.parseLong(message.substring(endIndex + "**-耗时".length(), perfIndex)
                .trim());
            PerfLog p = new PerfLog();
            p.setWhen(dateStr);
            p.setWhat(what);
            p.setAvg(perf);
            p.setMax(perf);
            p.setTotal(perf);
            p.setCount(1);
            ctx.invokeNext(ctx.buildEvent(e, p));
          }
        }).addLast("perfcounter", new MethodPerfCounter());
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
