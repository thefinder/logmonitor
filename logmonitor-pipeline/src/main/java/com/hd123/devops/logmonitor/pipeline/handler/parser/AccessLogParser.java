/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	AccessLogParser.java
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
import java.util.Locale;
import java.util.Map;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * 符合AccessLog格式的日志解析<br>
 * 输入：RawLog<br>
 * 输出：TokenLog<br>
 * 
 * @author zhangyanbo
 * 
 */
public class AccessLogParser implements LogHandler {
  private String pattern;
  private List<String> tokenList;
  private Map<String, String> tokenMapper = new HashMap();
  private String sourceDateFormat = "dd/MMM/yyyy:HH:mm:ss ZZZ";
  private Locale sourceLocale = Locale.US;
  private String dateFormat = "yyyy-MM-dd HH:mm:ss";

  public AccessLogParser() {
  }

  public AccessLogParser(String pattern) {
    this(pattern, null);
  }

  public AccessLogParser(String pattern, String[] tokenMapper) {
    setPattern(pattern);
    setTokenMapper(tokenMapper);
  }

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    if (e.getLog() instanceof RawLog) {
      TokenLog log = decode((RawLog) e.getLog());
      ctx.invokeNext(ctx.buildEvent(e, log));
    }
  }

  private TokenLog decode(RawLog r) throws Exception {
    if (r == null || r.getBody() == null)
      return new TokenLog();

    TokenLog log = new TokenLog();
    String s = new String(r.getBody(), "utf-8");
    int next = 0;
    for (int i = 0; i < tokenList.size(); i++) {
      String token = tokenList.get(i);
      String nextToken = i < tokenList.size() - 1 ? tokenList.get(i + 1) : null;
      if (nextToken != null && nextToken.startsWith("%"))
        nextToken = " ";

      if (token.startsWith("%")) {
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

  private int decodePart(String s, int start, String stopToken, String token, TokenLog log)
      throws Exception {
    if (stopToken == null) {
      String part = s.substring(start);
      setValue(log, token, part.trim());
      return -1;
    }

    int index = s.indexOf(stopToken, start);
    if (index < 0)
      return -1;

    String part = s.substring(start, index);
    setValue(log, token, part);
    return index;
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

  private void setValue(TokenLog log, String token, String value) throws Exception {
    String mappedToken = tokenMapper.get(token);
    if (mappedToken == null)
      mappedToken = token;

    if ("%b".equals(token)) {
      if ("-".equals(value))
        value = "0";
    } else if ("%t".equals(token)) {
      int start = 0;
      int end = value.length();
      if (value.startsWith("["))
        start++;
      if (value.endsWith("]"))
        end--;
      value = value.substring(start, end);
      Date d = new SimpleDateFormat(sourceDateFormat, sourceLocale).parse(value);
      value = new SimpleDateFormat(dateFormat).format(d);
    }
    log.setValue(mappedToken, value);
  }

  /**
   * 日志格式，例如：%h %l %u %t \"%r\" %s %b %D, 参见:
   * http://tomcat.apache.org/tomcat-5.5-doc/config/valve.html
   */
  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
    this.tokenList = decodePattern(pattern);
  }

  /** 字段映射，例如：["%h", "remotehost", "%r", "request"] */
  public void setTokenMapper(String[] tokenMapper) {
    this.tokenMapper.clear();
    if (tokenMapper != null) {
      for (int index = 0; index < tokenMapper.length - 1; index++) {
        this.tokenMapper.put(tokenMapper[index], tokenMapper[index + 1]);
      }
    }
  }

  /** 日期格式 */
  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

}
