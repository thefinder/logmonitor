/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	DefaultLogPipelineFactory.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.impl;

import com.hd123.devops.logmonitor.pipeline.LogPipeline;
import com.hd123.devops.logmonitor.pipeline.LogPipelineFactory;

/**
 * @author zhangyanbo
 * 
 */
public class DefaultLogPipelineFactory implements LogPipelineFactory {

  @Override
  public LogPipeline getPipeline() {
    return new DefaultLogPipeline();
  }

  @Override
  public LogPipeline getPipeline(String name) {
    return new DefaultLogPipeline(name);
  }

}
