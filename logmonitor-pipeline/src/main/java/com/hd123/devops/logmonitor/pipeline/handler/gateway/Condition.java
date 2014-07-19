/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	Condition.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.handler.gateway;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogHandlerContext;
import com.hd123.devops.logmonitor.pipeline.log.TokenLog;

/**
 * 条件判断
 * 
 * @author zhangyanbo
 * 
 */
public class Condition implements LogHandler {
  private final Logger logger = LoggerFactory.getLogger(Condition.class);

  private String token;
  private Pattern regex;

  public Condition(String token, String pattern) {
    this.token = token;
    this.regex = Pattern.compile(pattern);
  }

  @Override
  public void invoke(LogHandlerContext ctx, LogEvent e) throws Exception {
    if (e.getLog() == null || e.getLog() instanceof TokenLog == false)
      return;

    TokenLog log = (TokenLog) e.getLog();
    String value = log.getValue(token);
    logger.info(MessageFormat.format("判断: {0} = {1}, 条件：{2}", token, value, regex.toString()));
    if (value != null && regex.matcher(value).matches()) {
      logger.info(MessageFormat.format("判断: {0} = {1}, 条件：{2}，成立", token, value, regex.toString()));
      ctx.invokeNext(e);
    }
  }
}
