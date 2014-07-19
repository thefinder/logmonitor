/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	AccessLogTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-10 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.parser.AccessLogParser;
import com.hd123.devops.logmonitor.pipeline.handler.parser.FileParser;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * @author zhangyanbo
 * 
 */
public class AccessLogTest {

  @Test
  public void counter() throws Exception {
    final Map<String, Long> hostCounters = new TreeMap();
    String filename = "d:/work/perf/20140718/accesslog#soms#192.168.32.227.log";
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline().addLast("filecutter", new FileParser())
        .addLast("tokenizer", new AccessLogParser("%h %l %u %t \"%r\" %s %b %D"))
        .addLast("counter", new LogHandler() {
          @Override
          public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
            if (e.getLog() instanceof TokenLog == false)
              return;

            TokenLog log = (TokenLog) e.getLog();
            String dateStr = log.getValue("%t");
            String host = log.getValue("%h");
            String request = log.getValue("%r");
            String status = log.getValue("%s");
            if (dateStr == null || host == null || request == null)
              return;
            if (request.contains("/soms-server/rest/frontservice") == false)
              return;

            String key = host + "@" + dateStr.subSequence(0, 10) + "@" + request + "@" + status;
            Long c = hostCounters.get(key);
            if (c == null) {
              hostCounters.put(key, 1L);
            } else {
              hostCounters.put(key, c.longValue() + 1);
            }
          }
        });
    pipeline.handleEvent(new DefaultLogEvent(filename));
    for (Entry<String, Long> c : hostCounters.entrySet()) {
      System.out.println(StringUtils.leftPad(c.getValue().toString(), 10) + "    "
          + StringUtils.replace(c.getKey(), "@", "  "));
    }
  }
}
