/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	Cutter.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.handler.parser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * 以字符分割的简单日志切分器<br>
 * 输入：RawLog<br>
 * 输出：TokenLog<br>
 * 
 * @author zhangyanbo
 * 
 */
public class CharTokenizer implements LogHandler {
  private final Logger logger = LoggerFactory.getLogger(CharTokenizer.class);

  private String[] tokens;
  private char separator;

  public CharTokenizer(String[] tokens, char separator) {
    this.tokens = tokens;
    this.separator = separator;
  }

  public CharTokenizer(char separator) {
    this.separator = separator;
  }

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    logger.info("日志分割");

    TokenLog log = new TokenLog();
    if (e.getLog() instanceof RawLog && e.getLog() != null) {
      RawLog r = (RawLog) e.getLog();
      List<String> words = decodeLine(new String(r.getBody(), "utf-8"));
      if (tokens != null) {
        for (int index = 0; index < tokens.length && index < words.size(); index++) {
          log.setValue(tokens[index], words.get(index));
        }
      } else {
        int index = 1;
        for (String word : words) {
          log.setValue(String.valueOf(index++), word);
        }
      }
    }
    ctx.invokeNext(ctx.buildEvent(e, log));
  }

  private List<String> decodeLine(String line) {
    List<String> words = new ArrayList();
    boolean quoteOn = false;
    StringBuilder sb = new StringBuilder();
    for (char c : line.toCharArray()) {
      if (c == separator) {
        if (quoteOn) {
          sb.append(c);
        } else {
          words.add(decodeWord(sb.toString()));
          sb.setLength(0);
        }
      } else if (c == QUOTE) {
        quoteOn = !quoteOn;
        sb.append(c);
      } else
        sb.append(c);
    }
    words.add(decodeWord(sb.toString()));
    return words;
  }

  private String decodeWord(String word) {
    if (word == null)
      return "";
    String w = word.trim();
    boolean quoted = (w.charAt(0) == QUOTE && w.charAt(w.length() - 1) == QUOTE);
    if (quoted == false)
      return w;

    char[] cs = w.toCharArray();
    boolean quoteOn = false;
    StringBuilder sb = new StringBuilder();
    for (int index = 1; index < cs.length - 1; index++) {
      char c = cs[index];
      if (c == QUOTE) {
        if (quoteOn) {
          sb.append(c);
        }
        quoteOn = !quoteOn;
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  public String[] getTokens() {
    return tokens;
  }

  public void setTokens(String[] tokens) {
    this.tokens = tokens;
  }

  public char getSeparator() {
    return separator;
  }

  public void setSeparator(char separator) {
    this.separator = separator;
  }

  public static final char QUOTE = '"';
}
