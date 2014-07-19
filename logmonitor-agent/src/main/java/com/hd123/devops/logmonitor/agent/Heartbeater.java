/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	Detector.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-14 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangyanbo
 * 
 */
public class Heartbeater implements Runnable {
  private final Logger logger = LoggerFactory.getLogger(Heartbeater.class);
  private boolean debug = logger.isDebugEnabled();
  private Transporter transporter;
  private int heartbeatSeconds;

  public Heartbeater(Transporter transporter, int heartbeatSeconds) {
    this.transporter = transporter;
    this.heartbeatSeconds = heartbeatSeconds;
  }

  @Override
  public void run() {
    while (true) {
      if (TransportState.broken.equals(transporter.getState())) {
        try {
          transporter.reconnect();
        } catch (Exception e) {
          logger.error("", e);
        }
      }

      try {
        Thread.sleep(heartbeatSeconds * 1000);
      } catch (InterruptedException e) {
      }

      if (TransportState.connected.equals(transporter.getState())) {
        try {
          if (debug)
            logger.debug("ping " + transporter.getServer() + ":" + transporter.getPort());
          CommandQueue.send(new Command(Command.CMD_PING, null, null));
        } catch (InterruptedException e) {
        }
      }

      if (Thread.interrupted())
        break;
    }
  }

}