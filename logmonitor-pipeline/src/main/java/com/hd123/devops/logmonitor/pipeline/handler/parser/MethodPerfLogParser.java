/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	MethodPerfTokenizer.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-28 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.handler.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.log.PerfLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;
import com.hd123.rumba.commons.util.WildcardPattern;

/**
 * 基于方法的性能日志切分器
 * <p>
 * 输入(1)：TokenLog<br>
 * 输出(0..n)：PerfLog
 * 
 * @author zhangyanbo
 * 
 */
public class MethodPerfLogParser implements LogHandler {
  private List<WildcardPattern> methodPatterns = new ArrayList();
  private boolean aggByPath = false;
  private String messageToken = "message";
  private String dateToken = "date";

  public static final int TIMELINE_DATE = 10;
  public static final int TIMELINE_HOUR = 13;

  public MethodPerfLogParser(String[] methodPatterns) {
    setMethodPatterns(methodPatterns);
  }

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    if (e.getLog() instanceof TokenLog) {
      TokenLog log = (TokenLog) e.getLog();
      String dateStr = log.getValue(dateToken);
      String text = log.getValue(messageToken);
      if (dateStr == null || text == null)
        return;

      String[] lines = text.split("\r\n");
      for (String line : lines) {
        decodeLine(line, ctx, e, dateStr);
      }
    }
  }

  private void decodeLine(String line, LogHandlerContext ctx, LogEvent e, String dateStr)
      throws Exception {
    int m = line.lastIndexOf(":");
    if (m < 0)
      return;

    MethodPath methodPath = new MethodPath(line.substring(0, m).trim());
    if (methodPath.accept(methodPatterns) == false)
      return;

    String other = line.substring(m + 1);
    String[] pairs = other.split(",");
    Long time = null;
    Long count = null;
    for (String p : pairs) {
      String[] pair = p.split("=");
      if (pair.length >= 2) {
        String key = pair[0].trim();
        String value = pair[1].trim();
        if (KEY_TIME.equals(key)) {
          time = Long.parseLong(value);
        } else if (KEY_COUNT.equals(key)) {
          count = Long.parseLong(value);
        }
      }
    }

    if (time != null && count != null) {
      PerfLog p = new PerfLog();
      p.setWhen(dateStr);
      p.setWhat(aggMethod(methodPath));
      p.setCount(count.longValue());
      p.setTotal(time.longValue());
      p.setAvg(time.longValue() / count.longValue());
      p.setMax(time.longValue());
      ctx.invokeNext(ctx.buildEvent(e, p));
    }
  }

  private String aggMethod(MethodPath methodPath) {
    if (aggByPath)
      return methodPath.getPath();
    else
      return methodPath.getMethod();
  }

  /** 方法格式，例如["*com.hd123.MyService.saveNew()"] */
  public void setMethodPatterns(String[] methodPatterns) {
    for (String mp : methodPatterns)
      this.methodPatterns.add(new WildcardPattern(mp));
  }

  /** 是否按调用路径聚合 */
  public boolean isAggByPath() {
    return aggByPath;
  }

  public void setAggByPath(boolean aggByPath) {
    this.aggByPath = aggByPath;
  }

  public MethodPerfLogParser withAggByPath(boolean aggByPath) {
    setAggByPath(aggByPath);
    return this;
  }

  /** 信息字段 */
  public String getMessageToken() {
    return messageToken;
  }

  public void setMessageToken(String messageToken) {
    this.messageToken = messageToken;
  }

  /** 日期时间字段 */
  public String getDateToken() {
    return dateToken;
  }

  public void setDateToken(String dateToken) {
    this.dateToken = dateToken;
  }

  static class MethodPath {
    private List<String> methods = new ArrayList();
    private String path;

    MethodPath(String source) {
      String[] ss = source.split("/");
      for (int index = 0; index < ss.length; index++) {
        String s = ss[index];
        // 补完整包
        if (s.indexOf(".") < 0) {
          if (index > 0) {
            String prev = ss[index - 1];
            int pi = prev.indexOf("(");
            if (pi > 0) {
              pi = prev.lastIndexOf(".", pi);
              if (pi > 0) {
                s = prev.substring(0, pi) + "." + s;
              }
            }
          }
        }
        // 去方法参数
        int pi = s.indexOf("(");
        if (pi > 0)
          s = s.substring(0, pi) + "()";
        methods.add(s);
      }
      this.path = StringUtils.join(methods, "/");
    }

    boolean accept(List<WildcardPattern> methodPatterns) {
      for (WildcardPattern p : methodPatterns)
        if (p.matches(path))
          return true;
      return false;
    }

    String getPath() {
      return path;
    }

    String getMethod() {
      if (methods.isEmpty())
        return null;
      return methods.get(methods.size() - 1);
    }
  }

  public static final String KEY_TIME = "time";
  public static final String KEY_COUNT = "count";
}
