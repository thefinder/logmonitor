/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	TransportState.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-18 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

/**
 * @author zhangyanbo
 * 
 */
public enum TransportState {
  /** 未连接 */
  initial,
  /** 正在连接 */
  connecting,
  /** 连接成功 */
  connected,
  /** 连接断开 */
  broken;
}
