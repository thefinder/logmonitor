/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	MQPerfTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-11 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.parser.FileParser;
import com.hd123.devops.logmonitor.pipeline.handler.parser.Log4jParser;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;
import com.hd123.rumba.commons.json.JsonObject;

/**
 * @author zhangyanbo
 * 
 */
public class MQPerfTest {

  @Test
  public void test() throws Exception {
    String filename = "d:/work/perf/20140719/mq#soms#192.168.32.227.log";

    final Map<String, Long> counters = new TreeMap();

    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline()
        .addLast("filecutter", new FileParser("[0-9]{4}-[0-9]{2}-[0-9]{2}.*"))
        .addLast("log4jtokenizer", new Log4jParser("%d [%t] %p %c - %m%n"))
        .addLast("parser", new LogHandler() {
          @Override
          public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
            if (e.getLog() instanceof TokenLog == false)
              return;
            TokenLog log = (TokenLog) e.getLog();
            String dateStr = log.getValue("%d");
            String message = log.getValue("%m");
            String clazz = log.getValue("%c");
            if (dateStr == null || message == null)
              return;
            int start = message.indexOf("{");
            int end = message.lastIndexOf("}");
            if (start < 0 || end < 0)
              return;
            String jsonStr = message.substring(start, end + 1);
            JsonObject json = new JsonObject(jsonStr);
            String dateStr2 = json.optString("lastModified");
            if (dateStr2 == null)
              return;
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr.substring(0, 19));
            Date d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr2);
            long latency = (d.getTime() - d2.getTime()) / 1000;
            System.out.println(clazz + "    " + dateStr + "    " + dateStr2 + "    "
                + (d.getTime() - d2.getTime()) / 1000);

            if (latency < 0) {
              addCounter(counters, clazz + "@-1");
            } else if (latency < 2) {
              addCounter(counters, clazz + "@2");
            } else if (latency < 5) {
              addCounter(counters, clazz + "@5");
            } else if (latency < 60) {
              addCounter(counters, clazz + "@60");
            } else if (latency < 600) {
              addCounter(counters, clazz + "@600");
            } else {
              addCounter(counters, clazz + "@long");
            }
          }
        });
    pipeline.handleEvent(new DefaultLogEvent(filename));
    for (Entry<String, Long> c : counters.entrySet()) {
      System.out.println(StringUtils.leftPad(c.getValue().toString(), 10) + "    "
          + StringUtils.replace(c.getKey(), "@", "  "));
    }
  }

  private void addCounter(Map<String, Long> counters, String key) {
    Long c = counters.get(key);
    if (c == null)
      counters.put(key, 1L);
    else
      counters.put(key, c + 1);
  }
}
