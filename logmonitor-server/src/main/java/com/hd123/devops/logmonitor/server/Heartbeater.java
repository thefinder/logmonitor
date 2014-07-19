/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-server
 * 文件名：	Heartbeater.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-29 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳线程
 * 
 * @author zhangyanbo
 * 
 */
public class Heartbeater implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(Heartbeater.class);
  private int heartbeatSeconds;
  private String dataDir;

  public Heartbeater(int heartbeatSeconds, String dataDir) {
    this.heartbeatSeconds = heartbeatSeconds;
    this.dataDir = dataDir;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(heartbeatSeconds * 1000);
      } catch (InterruptedException e) {
      }

      saveAgentStatus();

      if (Thread.interrupted())
        break;
    }
  }

  private void saveAgentStatus() {
    try {
      File dir = new File(dataDir);
      if (dir.exists() == false)
        dir.mkdirs();

      File dataFile = new File(dataDir, "server-status");
      Properties props = new Properties();
      if (dataFile.exists()) {
        props.load(new FileInputStream(dataFile));
      }

      Set<String> servers = Acceptor.pingStatus.keySet();
      for (String server : servers) {
        Date d = Acceptor.pingStatus.get(server);
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
        props.setProperty("ping." + server, dateStr);
      }
      props.store(new FileOutputStream(dataFile), "");
    } catch (Exception e) {
      logger.error("保存状态出错。", e);
    }
  }
}
