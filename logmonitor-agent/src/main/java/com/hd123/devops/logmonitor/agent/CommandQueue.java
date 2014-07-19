/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	ChannelQueue.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-14 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyanbo
 * 
 */
public class CommandQueue {
  private static final SynchronousQueue<Command> queue = new SynchronousQueue();

  public static Command take() throws InterruptedException {
    return queue.take();
  }

  public static boolean send(Command content) throws InterruptedException {
    return queue.offer(content, 5, TimeUnit.SECONDS);
  }
}
