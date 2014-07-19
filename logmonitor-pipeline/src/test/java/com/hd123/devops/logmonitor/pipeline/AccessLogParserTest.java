/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	AccessLogParserTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-9 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.parser.AccessLogParser;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * @author zhangyanbo
 * 
 */
public class AccessLogParserTest {

  @Test
  public void date() throws Exception {
    System.out.println(new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.UK).format(new Date()));
    System.out.println(new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.UK)
        .parse("07/Jul/2014:13:25:06 +0800"));
  }

  @Test
  public void test() throws Exception {
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline();
    final List<TokenLog> logs = new ArrayList();
    pipeline.addLast("parser", new AccessLogParser("%h %l %u %t \"%r\" %s %b %D"));
    pipeline.addLast("test", new LogHandler() {
      @Override
      public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
        if (e.getLog() instanceof TokenLog) {
          logs.add((TokenLog) e.getLog());
        }
      }
    });

    String s = "127.0.0.1 - guest [07/Jul/2014:13:25:06 +0800] \"GET /logviewer/extjs/locale/ext-lang-zh_CN.js HTTP/1.1\" 200 9660 11";
    RawLog log = new RawLog();
    log.setBody(s.getBytes());
    pipeline.handleEvent(new DefaultLogEvent(log));
    Assert.assertEquals(logs.size(), 1);
    TokenLog t = logs.get(0);
    Assert.assertEquals(t.getValue("%h"), "127.0.0.1");
    Assert.assertEquals(t.getValue("%l"), "-");
    Assert.assertEquals(t.getValue("%u"), "guest");
    Assert.assertEquals(t.getValue("%t"), "2014-07-07 13:25:06");
    Assert.assertEquals(t.getValue("%r"), "GET /logviewer/extjs/locale/ext-lang-zh_CN.js HTTP/1.1");
    Assert.assertEquals(t.getValue("%s"), "200");
    Assert.assertEquals(t.getValue("%b"), "9660");
    Assert.assertEquals(t.getValue("%D"), "11");
  }
}
