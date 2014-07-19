/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	LuceneTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-17 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.parser.FileParser;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;

/**
 * @author zhangyanbo
 * 
 */
public class LuceneTest {

  @Test
  public void count() throws Exception {
    final Map<String, Long> counters = new HashMap();

    String filename = "d:/work/search/export.txt";
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline().addLast("filecutter", new FileParser())
        .addLast("parser", new LogHandler() {
          private boolean matches = false;

          @Override
          public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
            if (e.getLog() instanceof RawLog == false)
              return;
            RawLog log = (RawLog) e.getLog();
            String s = new String(log.getBody(), "utf-8");
            if (s.contains("'orderId' norm=")) {
              matches = true;
            } else if (matches) {
              int start = s.indexOf("<val>");
              int end = s.indexOf("</val>");
              if (start >= 0 && end > start) {
                String orderId = s.substring(start + 5, end);
                Long c = counters.get(orderId);
                if (c != null)
                  counters.put(orderId, c.longValue() + 1);
                else
                  counters.put(orderId, 1L);
              }
              matches = false;
            }
          }
        });
    pipeline.handleEvent(new DefaultLogEvent(filename));
    for (Entry<String, Long> c : counters.entrySet()) {
      if (c.getValue().longValue() > 1L) {
        System.out.println(c.getKey() + "    " + c.getValue());
      }
    }
  }
}
