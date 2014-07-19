/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	FileLogger.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.handler.updater;

import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;

/**
 * 记录到文件日志<br>
 * 输入：RawLog<br>
 * 输出：RawLog
 * 
 * @author zhangyanbo
 * 
 */
public class FileLogger implements LogHandler {
  private final Logger logger = LoggerFactory.getLogger(FileLogger.class);

  private String outputDir;

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    if (e.getLog() instanceof RawLog) {
      RawLog log = (RawLog) e.getLog();
      File file = getLogFile(log);
      if (file.exists() == false)
        file.createNewFile();

      logger.info("写文件： " + file.getName());
      FileOutputStream fos = new FileOutputStream(file, true);
      try {
        fos.write(log.getBody());
        fos.flush();
      } finally {
        fos.close();
      }
    }

    ctx.invokeNext(e);
  }

  private File getLogFile(RawLog m) {
    File dir = new File(outputDir);
    dir.mkdirs();

    String filename = m.getContext();
    if (filename != null) {
      filename = filename.replaceAll("/", "#");
      filename = filename.replaceAll("\\\\", "#");
    } else
      filename = "unknown";
    return new File(dir, filename + ".log");
  }

  /** 日志输出目录 */
  public String getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

}
