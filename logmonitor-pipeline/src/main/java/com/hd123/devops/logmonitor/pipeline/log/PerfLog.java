/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	PerfLog.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-28 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.log;

/**
 * 性能日志
 * 
 * @author zhangyanbo
 * 
 */
public class PerfLog {

  private String when;
  private String what;
  private long total = 0L;
  private long avg = 0L;
  private long max = 0L;
  private long count = 0L;

  /** 什么时间 */
  public String getWhen() {
    return when;
  }

  public void setWhen(String when) {
    this.when = when;
  }

  /** 什么事 */
  public String getWhat() {
    return what;
  }

  public void setWhat(String what) {
    this.what = what;
  }

  /** 总耗时 */
  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  /** 平均耗时 */
  public long getAvg() {
    return avg;
  }

  public void setAvg(long avg) {
    this.avg = avg;
  }

  /** 最大耗时 */
  public long getMax() {
    return max;
  }

  public void setMax(long max) {
    this.max = max;
  }

  /** 运行次数 */
  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }
}
