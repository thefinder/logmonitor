/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	Log4jParser.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-27 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.handler.parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * 符合Log4j格式的日志切分器<br>
 * 输入：RawLog, TokenLog<br>
 * 输出：TokenLog<br>
 * 
 * @author zhangyanbo
 * 
 */
public class Log4jParser implements LogHandler {
  private String pattern;
  private List<String> tokenList;
  private Map<String, String> tokenMapper = new HashMap();
  private String encoding = "utf-8";
  private String sourceDateFormat = "yyyy-MM-dd HH:mm:ss,SSS";
  private String dateFormat = "yyyy-MM-dd HH:mm:ss";

  public Log4jParser(String pattern) {
    setPattern(pattern);
  }

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    if (e.getLog() instanceof RawLog) {
      TokenLog log = decode((RawLog) e.getLog());
      ctx.invokeNext(ctx.buildEvent(e, log));
    } else if (e.getLog() instanceof TokenLog) {
      ctx.invokeNext(e);
    }
  }

  private TokenLog decode(RawLog r) throws Exception {
    if (r == null || r.getBody() == null)
      return new TokenLog();

    TokenLog log = new TokenLog();
    String s = new String(r.getBody(), encoding);
    int next = 0;
    for (int i = 0; i < tokenList.size(); i++) {
      String token = tokenList.get(i);
      String nextToken = i < tokenList.size() - 1 ? tokenList.get(i + 1) : null;
      if ("%n".equals(nextToken))
        nextToken = null;
      else if (nextToken != null && nextToken.startsWith("%"))
        nextToken = " ";

      if ("%n".equals(token)) {
        // do nothing
      } else if ("%d".equals(token)) {
        next = decodeDate(s, next, log);
      } else if (token.startsWith("%")) {
        next = decodePart(s, next, nextToken, token, log);
      } else {
        if (s.startsWith(token, next))
          next += token.length();
        else
          break;
      }

      if (next < 0 || next >= s.length())
        break;
    }
    return log;
  }

  private int decodeDate(String s, int start, TokenLog log) throws Exception {
    int next = s.indexOf(" ", start);
    if (next < 0)
      return -1;
    next = s.indexOf(" ", next + 1);
    if (next < 0)
      return -1;

    String part = s.substring(start, next);
    setValue(log, "%d", part);

    return next;
  }

  private int decodePart(String s, int start, String stopToken, String token, TokenLog log)
      throws Exception {
    if (stopToken == null) {
      String part = s.substring(start);
      setValue(log, token, part);
      return -1;
    }

    int index = s.indexOf(stopToken, start);
    if (index < 0)
      return -1;

    String part = s.substring(start, index);
    setValue(log, token, part);
    return index;
  }

  private void setValue(TokenLog log, String token, String value) throws Exception {
    String mappedToken = tokenMapper.get(token);
    if (mappedToken == null)
      mappedToken = token;

    if ("%d".equals(token)) {
      Date d = new SimpleDateFormat(sourceDateFormat).parse(value);
      value = new SimpleDateFormat(dateFormat).format(d);
    }
    log.setValue(mappedToken, value);
  }

  private List<String> decodePattern(String s) {
    List<String> result = new ArrayList();
    StringBuilder sb = new StringBuilder();
    char[] cs = s.toCharArray();
    boolean tag = false;
    for (int index = 0; index < cs.length; index++) {
      char c = cs[index];
      if (c == '%') {
        tag = true;
        if (sb.length() > 0) {
          result.add(sb.toString());
          sb.setLength(0);
        }
      } else {
        if (tag) {
          result.add("%" + c);
          tag = false;
        } else {
          sb.append(c);
        }
      }
    }
    if (sb.length() > 0)
      result.add(sb.toString());
    return result;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /** 日志格式，例如：%d [%t] %p %c - %m%n */
  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
    this.tokenList = decodePattern(pattern);
  }

  /** 字段映射，例如：["%p", "thread", "%c", "category"] */
  public void setTokenMapper(String[] tokenMapper) {
    this.tokenMapper.clear();
    if (tokenMapper != null) {
      for (int index = 0; index < tokenMapper.length - 1; index++) {
        this.tokenMapper.put(tokenMapper[index], tokenMapper[index + 1]);
      }
    }
  }

  public Log4jParser withTokenMapper(String... tokenMapper) {
    setTokenMapper(tokenMapper);
    return this;
  }

  public Log4jParser withEncoding(String encoding) {
    setEncoding(encoding);
    return this;
  }
}
