/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	AccessLogTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-29 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.analyzer.AccessLogPerfCounter;
import com.hd123.devops.logmonitor.pipeline.handler.parser.AccessLogParser;
import com.hd123.devops.logmonitor.pipeline.handler.parser.FileParser;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.PerfLog;

/**
 * @author zhangyanbo
 * 
 */
public class AccessLogPerfTest {

  @Test
  public void test() throws Exception {
    String filename = "d:/work/perf/20140628/accesslog#soms#front#192.168.32.227.log";
    LogPipeline pipeline = buildPipeline();
    pipeline.handleEvent(new DefaultLogEvent(filename));
    print(pipeline);
  }

  private LogPipeline buildPipeline() {
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    return factory
        .getPipeline()
        .addLast("filecutter", new FileParser())
        .addLast("accesslogtokenizer",
            new AccessLogParser("%h %l %u %t &quot;%r&quot; %s %b %D"))
        .addLast("perfcounter", new AccessLogPerfCounter());
  }

  public void print(LogPipeline pipeline) {
    AccessLogPerfCounter handler = (AccessLogPerfCounter) pipeline.get("perfcounter");
    List<PerfLog> list = new ArrayList(handler.getResult());
    Collections.sort(list, new Comparator<PerfLog>() {
      @Override
      public int compare(PerfLog o1, PerfLog o2) {
        return o1.getWhat().compareTo(o2.getWhat());
      }
    });

    for (PerfLog c : list) {
      System.out.println(MessageFormat.format("{0}: total={1}, avg={2}, max={3}, count={4}",
          c.getWhat(), c.getTotal(), c.getAvg(), c.getMax(), c.getCount()));
    }
  }
}
