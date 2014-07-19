/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	FileParser.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-28 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.handler.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;

/**
 * 文件切分器
 * <p>
 * 输入：文件名<br>
 * 输出：RawLog
 * 
 * @author zhangyanbo
 * 
 */
public class FileParser implements LogHandler {
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  public FileParser() {
  }

  public FileParser(String newLinePattern) {
    setNewLinePattern(newLinePattern);
  }

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    if (e.getLog() instanceof String) {
      decodeFile((String) e.getLog(), ctx, e);
    }
  }

  private void decodeFile(String filename, LogHandlerContext ctx, LogEvent e) throws Exception {
    Reader r = null;
    if (fileEncoding != null)
      r = new InputStreamReader(new FileInputStream(filename), fileEncoding);
    else
      r = new InputStreamReader(new FileInputStream(filename));
    BufferedReader reader = new BufferedReader(r);
    try {
      StringBuffer sb = new StringBuffer();
      String line = null;
      long count = 0L;
      while ((line = reader.readLine()) != null) {
        if (isDetailLog(line)) {
          if (sb.length() > 0)
            sb.append("\r\n");
          sb.append(line);
        } else {
          fireEvent(sb, ctx, e);
          sb.setLength(0);
          sb.append(line);
        }
        count++;
        if (count % 10000 == 0)
          logger.info("行 " + count);
      }
      fireEvent(sb, ctx, e);
    } finally {
      reader.close();
    }
  }

  private void fireEvent(StringBuffer sb, LogHandlerContext ctx, LogEvent e) throws Exception {
    if (sb.length() == 0)
      return;

    RawLog log = new RawLog();
    log.setBody(sb.toString().getBytes(encoding));
    ctx.invokeNext(ctx.buildEvent(e, log));
  }

  private boolean isDetailLog(String s) {
    if (newLinePattern == null)
      return false;

    return s != null && (newLinePattern.matcher(s).matches() == false);
  }

  /** 行分割，如果为null表示每行是一行日志 */
  public String getNewLinePattern() {
    return newLinePattern != null ? newLinePattern.pattern() : null;
  }

  public void setNewLinePattern(String pattern) {
    if (pattern == null)
      this.newLinePattern = null;
    else
      this.newLinePattern = Pattern.compile(pattern);
  }

  /** 文件编码格式 */
  public String getFileEncoding() {
    return fileEncoding;
  }

  public void setFileEncoding(String fileEncoding) {
    this.fileEncoding = fileEncoding;
  }

  public FileParser withFileEncoding(String fileEncoding) {
    setFileEncoding(fileEncoding);
    return this;
  }

  /** 日志内容编码格式 */
  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public FileParser withEncoding(String encoding) {
    setEncoding(encoding);
    return this;
  }

  private Pattern newLinePattern;
  private String encoding = "utf-8";
  private String fileEncoding = null;
}
