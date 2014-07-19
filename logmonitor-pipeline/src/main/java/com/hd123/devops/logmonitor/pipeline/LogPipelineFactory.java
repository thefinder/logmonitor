/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	LogPipelineFactory.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline;

/**
 * 流水线工厂
 * 
 * @author zhangyanbo
 * 
 */
public interface LogPipelineFactory {

  /**
   * 产生一条新的流水线。
   * 
   * @return 流水线。
   */
  LogPipeline getPipeline();
  
  /**
   * 产生一条指定名称新的流水线。
   * 
   * @return 流水线。
   */
  LogPipeline getPipeline(String name);
}
