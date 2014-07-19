/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	MethodPerfCounter.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-27 - zhangyanbo - 创建。
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

/**
 * 方法性能统计 <br>
 * 输入：PerfLog<br>
 * 输出：PerfLog<br>
 * 
 * @author zhangyanbo
 * 
 */
public class MethodPerfCounter implements LogHandler {

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    if (e.getLog() instanceof PerfLog) {
      PerfLog log = (PerfLog) e.getLog();
      if (log != null)
        addCounter(log);

      ctx.invokeNext(e);
    }
  }

  public Collection<PerfLog> getResult() {
    return Collections.unmodifiableCollection(counters.values());
  }

  private String aggregate(String when) {
    return null;
  }

  private void addCounter(PerfLog log) {
    String when = aggregate(log.getWhen());

    PerfLog c = counters.get(when + "@" + log.getWhat());
    if (c == null) {
      c = new PerfLog();
      c.setWhen(when);
      c.setWhat(log.getWhat());
      counters.put(c.getWhen() + "@" + c.getWhat(), c);
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

  private static Map<String, PerfLog> counters = new HashMap();
}
