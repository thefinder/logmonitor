/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	AccessLogPerfCounter.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-29 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.handler.analyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.log.PerfLog;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * AccessLog的性能计数
 * <p>
 * 输入：TokenLog<br>
 * 输出：PerfLog
 * 
 * @author zhangyanbo
 * 
 */
public class AccessLogPerfCounter implements LogHandler {
  private String dateToken = "%t";
  private String requestToken = "%r";
  private String perfToken = "%D";
  private boolean byHour = false;

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    if (e.getLog() instanceof TokenLog) {
      TokenLog log = (TokenLog) e.getLog();
      String dateStr = log.getValue(dateToken);
      String requestStr = log.getValue(requestToken);
      String perfStr = log.getValue(perfToken);
      if (dateStr != null && requestStr != null && perfStr != null) {
        if (byHour)
          dateStr = dateStr.substring(0, 13);
        else
          dateStr = dateStr.substring(0, 10); // 只到日期
        long perf = Long.parseLong(perfStr);

        PerfLog p = new PerfLog();
        p.setWhen(dateStr);
        p.setWhat(requestStr);
        p.setAvg(perf);
        p.setTotal(perf);
        p.setMax(perf);
        p.setCount(1);
        addCounter(p);

        ctx.invokeNext(ctx.buildEvent(e, p));
      }
    }
  }

  private void addCounter(PerfLog log) {
    PerfLog c = counters.get(log.getWhat());
    if (c == null) {
      c = new PerfLog();
      c.setWhat(log.getWhat());
      counters.put(c.getWhat(), c);
    }

    long total = c.getTotal() + log.getTotal();
    long newCount = c.getCount() + log.getCount();
    long newAvg = total / newCount;
    c.setTotal(total);
    c.setAvg(newAvg);
    c.setCount(newCount);
    if (log.getMax() > c.getMax())
      c.setMax(log.getMax());
  }

  public Collection<PerfLog> getResult() {
    return Collections.unmodifiableCollection(counters.values());
  }

  /** 按小时统计 */
  public boolean isByHour() {
    return byHour;
  }

  public void setByHour(boolean byHour) {
    this.byHour = byHour;
  }

  private static Map<String, PerfLog> counters = new HashMap();
}
