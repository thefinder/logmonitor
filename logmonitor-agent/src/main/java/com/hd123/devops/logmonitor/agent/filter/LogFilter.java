/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	LogFilter.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-9 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent.filter;

/**
 * 日志过滤器
 * 
 * @author zhangyanbo
 * 
 */
public interface LogFilter {

  /**
   * 当前行是否满足条件。
   * 
   * @param line
   *          行
   * @return
   * @throws Exception
   */
  public boolean accept(String line) throws Exception;
}
