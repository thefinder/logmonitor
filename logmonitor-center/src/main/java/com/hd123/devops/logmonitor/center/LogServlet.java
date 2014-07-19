/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-center
 * 文件名：	LogServlet.java
 * 模块说明：	
 * 修改历史：
 * 2014-7-10 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.center;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hd123.devops.logmonitor.pipeline.LogPipeline;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;

/**
 * @author zhangyanbo
 * 
 */
@Controller
public class LogServlet implements ApplicationContextAware {
  private Logger logger = LoggerFactory.getLogger(getClass());

  public static final String PIPELINE = "pipeline";

  @RequestMapping(value = "/submit", method = RequestMethod.POST, consumes = "application/json; charset=utf-8")
  @ResponseBody
  public String submit(@RequestBody
  RawLog log) {
    if (log == null)
      return "null";

    try {
      LogPipeline pipeline = appCtx.getBean(PIPELINE, LogPipeline.class);
      pipeline.handleEvent(new DefaultLogEvent(log));
      return "ok";
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException("提交日志出错，请联系系统管理员。");
    }
  }

  @RequestMapping(value = "/status", method = RequestMethod.GET)
  @ResponseBody
  public String ping() {
    return "Log Center is ok.";
  }

  private ApplicationContext appCtx;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.appCtx = applicationContext;
  }

}
