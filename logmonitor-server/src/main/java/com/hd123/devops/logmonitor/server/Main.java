/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-server
 * 文件名：	Main.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-14 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.hd123.devops.logmonitor.pipeline.LogPipeline;

/**
 * @author zhangyanbo
 * 
 */
public class Main {
  private final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      printUsage();
      return;
    }

    File file = new File(args[0]);
    if (file.exists() == false) {
      System.out.println("找不到配置文件：" + args[0]);
      return;
    }

    // 配置log4j
    PropertyConfigurator.configure(file.getParent() + "/logmonitor-server.properties");
    new Main().run(file);
  }

  public void run(File configFile) throws Exception {
    this.dataDir = configFile.getParent() + "/../data";

    logger.info("加载配置： " + configFile);
    InputStream is = new FileInputStream(configFile);
    try {
      loadConfig(is);
    } finally {
      is.close();
    }

    String pipelineFile = configFile.getParent() + "/logmonitor-pipeline.xml";
    loadPipeline(pipelineFile);

    start();
  }

  private void start() throws Exception {
    logger.info("启动日志服务");
    acceptor = new Acceptor(port, pipeline);
    acceptor.start();
    heartbeater = new Heartbeater(heartbeatSeconds, dataDir);
    Thread t = new Thread(heartbeater);
    t.setDaemon(true);
    t.start();
  }

  private void loadConfig(InputStream is) throws Exception {
    Properties props = new Properties();
    props.load(is);

    port = Integer.parseInt(props.getProperty(KEY_SERVER_PORT, "15880"));

    this.heartbeatSeconds = Integer.parseInt(props.getProperty(KEY_HEARTBEAT_SECONDS, "30"));
    this.heartbeatSeconds = Math.max(5, heartbeatSeconds);

    StringBuffer sb = new StringBuffer();
    sb.append("启动参数：").append("\r\n");
    sb.append("=======================================\r\n");
    sb.append("  logmonitor.server.port = " + port).append("\r\n");
    sb.append("=======================================");
    logger.info(sb.toString());
  }

  private void loadPipeline(String filename) throws Exception {
    logger.info("加载Pipeline: " + filename);
    File f = new File(filename);
    if (f.exists() == false)
      throw new Exception("找不到Pipeline文件：" + filename);

    ApplicationContext appCtx = new FileSystemXmlApplicationContext(filename);
    pipeline = appCtx.getBean("pipeline", LogPipeline.class);
  }

  private static void printUsage() {
    System.out.println("logmonitor server");
    System.out.println("使用方法：java Main <config_file>");
    System.out.println("  config_file 主配置文件。");
  }

  private int port = 15880;
  private int heartbeatSeconds = 30; // 心跳时间
  private Acceptor acceptor;
  private Heartbeater heartbeater;
  private LogPipeline pipeline;
  private String dataDir;

  private static final String KEY_HEARTBEAT_SECONDS = "logmonitor.heartbeatseconds";
  private static final String KEY_SERVER_PORT = "logmonitor.server.port";

}
