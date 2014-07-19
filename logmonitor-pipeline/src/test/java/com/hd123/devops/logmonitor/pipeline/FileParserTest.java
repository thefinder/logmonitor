/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	FileCutterTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-9 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.hd123.devops.logmonitor.pipeline.handler.parser.FileParser;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogPipelineFactory;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;

/**
 * @author zhangyanbo
 * 
 */
public class FileParserTest {

  @Test
  public void test() throws Exception {
    LogPipelineFactory factory = new DefaultLogPipelineFactory();
    LogPipeline pipeline = factory.getPipeline();

    FileParser parser = new FileParser();
    pipeline.addLast("parser", parser);
    final List<String> outputs = new ArrayList();
    pipeline.addLast("test", new LogHandler() {
      @Override
      public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
        if (e.getLog() instanceof RawLog) {
          RawLog log = (RawLog) e.getLog();
          outputs.add(new String(log.getBody(), "utf-8"));
        }
      }
    });

    File tempFile = File.createTempFile("test", ".log");
    try {
      writeFile(tempFile, new String[] {
          "a", "b" });
      pipeline.handleEvent(new DefaultLogEvent(tempFile.getAbsolutePath()));
    } finally {
      tempFile.delete();
    }

    Assert.assertEquals(outputs.size(), 2);
    Assert.assertEquals(outputs.get(0), "a");
    Assert.assertEquals(outputs.get(1), "b");
  }

  private void writeFile(File file, String[] lines) throws Exception {
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
    try {
      for (String line : lines) {
        writer.write(line);
        writer.write("\r\n");
      }
      writer.flush();
    } finally {
      writer.close();
    }
  }
}
