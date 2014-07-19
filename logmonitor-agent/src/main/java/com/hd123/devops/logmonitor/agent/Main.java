/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	Main.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-9 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.devops.logmonitor.agent.filter.PatternFilter;
import com.hd123.devops.logmonitor.agent.filter.WordFilter;

/**
 * 
 * @author zhangyanbo
 * 
 */
public class Main implements Runnable {
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
    PropertyConfigurator.configure(file.getParent() + "/logmonitor.properties");
    new Main().run(file);
  }

  public void run(File configFile) throws Exception {
    logger.info("加载配置： " + configFile);
    InputStream is = new FileInputStream(configFile);
    try {
      loadConfig(is);
    } finally {
      is.close();
    }

    start();
  }

  private void start() throws Exception {
    transporter = new Transporter(logServer, logServerPort);
    transporter.start(this);
    heartbeater = new Heartbeater(transporter, heartbeatSeconds);
    Thread t = new Thread(heartbeater);
    t.setDaemon(true);
    t.start();
  }

  private void loadConfig(InputStream is) throws Exception {
    Properties props = new Properties();
    props.load(is);

    this.threadCount = Integer.parseInt(props.getProperty(KEY_THREAD_COUNT, "1"));
    this.threadCount = Math.max(1, Math.min(threadCount, 10));

    this.scandSeconds = Integer.parseInt(props.getProperty(KEY_SCAN_SECONDS, "60"));
    this.scandSeconds = Math.max(1, scandSeconds);

    this.heartbeatSeconds = Integer.parseInt(props.getProperty(KEY_HEARTBEAT_SECONDS, "30"));
    this.heartbeatSeconds = Math.max(5, heartbeatSeconds);

    String temp = props.getProperty(KEY_SERVER, "").trim();
    int idx = temp.indexOf(":");
    if (idx > 0) {
      this.logServer = temp.substring(0, idx);
      this.logServerPort = Integer.parseInt(temp.substring(idx + 1));
    } else
      this.logServer = temp;

    this.host = props.getProperty(KEY_HOST, "").trim();

    Set<String> collectorIds = new HashSet();
    Set<Object> keys = props.keySet();
    for (Object o : keys) {
      String key = (String) o;
      if (key.startsWith(PREFIX_COLLECTOR)) {
        String s = key.substring(PREFIX_COLLECTOR.length());
        int index = s.indexOf(".");
        String collectorId = s.substring(0, index);
        collectorIds.add(collectorId);
      }
    }

    for (String collectorId : collectorIds) {
      Collector c = new Collector();
      c.setId(collectorId);
      c.getFile().setNamePattern(
          props.getProperty(MessageFormat.format(KEY_COLLECTOR_FILE, collectorId)));
      c.setNewLinePattern(Pattern.compile(props.getProperty(
          MessageFormat.format(KEY_COLLECTOR_NEWLINE_PATTERN, collectorId), ".*")));
      c.setLogContext(props.getProperty(
          MessageFormat.format(KEY_COLLECTOR_LOGCONTEXT, collectorId), collectorId).trim());

      String filterClass = props.getProperty(MessageFormat
          .format(KEY_COLLECTOR_FILTER, collectorId));
      if (WordFilter.class.getName().equals(filterClass)) {
        c.setFilter(loadWordFilterConfig(props,
            MessageFormat.format(KEY_COLLECTOR_FILTER_WORD, collectorId)));
      } else if (PatternFilter.class.getName().equals(filterClass)) {
        c.setFilter(loadPatternFilterConfig(props,
            MessageFormat.format(KEY_COLLECTOR_FILTER_PATTERN, collectorId)));
      } else {
        logger.error(collectorId + " 没有设置filter");
        continue;
      }

      collectors.add(c);
    }

    StringBuilder sb = new StringBuilder();
    sb.append("启动参数：").append("\r\n");
    sb.append("=======================================\r\n");
    sb.append("  ").append(KEY_SERVER).append(" = ").append(logServer + ":" + logServerPort)
        .append("\r\n");
    sb.append("  ").append(KEY_HOST).append(" = ").append(host).append("\r\n");
    sb.append("  ").append(KEY_THREAD_COUNT).append(" = ").append(threadCount).append("\r\n");
    sb.append("  ").append(KEY_SCAN_SECONDS).append(" = ").append(scandSeconds).append("\r\n");
    if (collectors.isEmpty())
      sb.append("  监视 0 个 日志文件。\r\n");
    else {
      sb.append("  监视 " + collectors.size() + " 个日志文件：\r\n");
      for (Collector c : collectors)
        sb.append("  ").append(c.toString()).append("\r\n");
    }
    sb.append("=======================================");
    logger.info(sb.toString());
  }

  private WordFilter loadWordFilterConfig(Properties props, String key) throws Exception {
    WordFilter filter = new WordFilter();
    String word = props.getProperty(key);
    if (word != null)
      filter.addWord(new String(word.getBytes("ISO8859-1")));

    Set<Object> keys = props.keySet();
    for (Object o : keys) {
      String k = (String) o;
      if (k.startsWith(key + ".")) {
        word = props.getProperty(k);
        if (word != null)
          filter.addWord(new String(word.getBytes("ISO8859-1")));
      }
    }
    return filter;
  }

  private PatternFilter loadPatternFilterConfig(Properties props, String key) {
    PatternFilter filter = new PatternFilter();
    String pattern = props.getProperty(key);
    if (pattern != null)
      filter.addPattern(pattern);

    Set<Object> keys = props.keySet();
    for (Object o : keys) {
      String k = (String) o;
      if (k.startsWith(key + ".")) {
        pattern = props.getProperty(k);
        if (pattern != null)
          filter.addPattern(pattern);
      }
    }
    return filter;
  }

  private static void printUsage() {
    System.out.println("logmonitor agent");
    System.out.println("使用方法：java Main <config_file>");
    System.out.println("  config_file 主配置文件。");
  }

  @Override
  public void run() {
    if (exec != null)
      return;

    logger.info("启动监视线程: " + threadCount + " 个");
    // 启动一个线程每xx秒钟读取新增的日志信息
    exec = Executors.newScheduledThreadPool(threadCount);
    for (final Collector c : collectors) {
      recoverCollectorState(c);
      exec.scheduleWithFixedDelay(new Runnable() {
        public void run() {
          try {
            readLog(c);
            if (c.getFile().tryRolling()) { // 文件滚动
              logger.info("滚动日志：" + c.getFile().getFileName());
            }
          } catch (Exception e) {
            logger.error("读取文件异常: " + c.getFile().getFileName(), e);
          } finally {
            if (c.getFile().isPositionChanged())
              storeCollectorState(c);
          }
        }
      }, 0, scandSeconds, TimeUnit.SECONDS);
    }
  }

  private void readLog(Collector c) throws Exception {
    if (c.getFile().exists() == false) {
      CommandQueue.send(new Command(Command.CMD_PING, c.getLogContext(),
          (c.getFile().getFileName() + " not exists").getBytes()));
      return;
    }

    if (logger.isDebugEnabled())
      logger.debug("定位日志文件 " + c.getFile().getFileName());
    if (c.getFile().modified() == false) // 如果文件时间戳未变，不读取
      return;

    // 指定文件可读可写
    final RandomAccessFile randomFile = new RandomAccessFile(c.getFile().getFile(), "r");
    try {
      long currentModified = c.getFile().getFile().lastModified();
      String currentHeadLine = randomFile.readLine();
      if (c.getFile().headLineChanged(currentHeadLine)) {
        logger.info(c.getFile().getFileName() + " 重置读取位置");
        c.getFile().resetPosition();
      }

      // 获得变化部分的
      logger.debug("读文件 " + c.getFile().getFileName() + "，位置：" + c.getFile().getLastPosition());
      randomFile.seek(c.getFile().getLastPosition());
      String tmp = "";
      boolean detailLog = false;
      List<byte[]> buffer = new ArrayList();
      while ((tmp = randomFile.readLine()) != null) {
        byte[] line = tmp.getBytes("ISO8859-1");
        String s = new String(line);
        if (detailLog && c.isDetailLog(s)) {
          buffer.add(line);
        } else if (c.getFilter().accept(s)) {
          if (sendBuffer(randomFile, buffer, c) == false)
            return;
          c.getFile().movePosition(currentModified, randomFile.getFilePointer(), currentHeadLine);

          detailLog = true;
          buffer.add(line);
        } else {
          detailLog = false;
        }
      }
      if (sendBuffer(randomFile, buffer, c) == false)
        return;
      c.getFile().movePosition(currentModified, randomFile.getFilePointer(), currentHeadLine);
    } finally {
      randomFile.close();
    }
  }

  private void recoverCollectorState(Collector c) {
    try {
      File dir = new File(dataDir);
      if (dir.exists() == false)
        return;

      File dataFile = new File(dataDir, "agent-status");
      if (dataFile.exists() == false)
        return;

      logger.info("恢复 " + c.getId() + " 状态");
      Properties props = new Properties();
      props.load(new FileInputStream(dataFile));
      c.getFile().movePosition(Long.parseLong(props.getProperty(c.getId() + ".lastmodified", "0")),
          Long.parseLong(props.getProperty(c.getId() + ".lastpos", "0")),
          props.getProperty(c.getId() + ".headline", ""));
    } catch (Exception e) {
      logger.error("恢复状态出错。", e);
    }
  }

  private synchronized void storeCollectorState(Collector c) {
    try {
      File dir = new File(dataDir);
      if (dir.exists() == false)
        dir.mkdirs();

      File dataFile = new File(dataDir, "agent-status");
      Properties props = new Properties();
      if (dataFile.exists()) {
        props.load(new FileInputStream(dataFile));
      }

      props.setProperty(c.getId() + ".lastpos", String.valueOf(c.getFile().getLastPosition()));
      props.setProperty(c.getId() + ".headline", c.getFile().getLastHeadLine());
      props.setProperty(c.getId() + ".lastmodified", String.valueOf(c.getFile().getLastModified()));
      props.store(new FileOutputStream(dataFile), "");
    } catch (Exception e) {
      logger.error("保存状态出错。", e);
    }
  }

  private boolean sendBuffer(RandomAccessFile randomFile, List<byte[]> buffer, Collector collector)
      throws Exception {
    if (buffer.isEmpty())
      return true;

    int len = 0;
    for (byte[] o : buffer)
      len += o.length + 2;
    byte[] arr = new byte[len];
    int pos = 0;
    for (byte[] o : buffer) {
      System.arraycopy(o, 0, arr, pos, o.length);
      pos += o.length;
      arr[pos++] = 13;
      arr[pos++] = 10;
    }

    logger.info("[" + collector.getId() + "] send " + arr.length + ", location: "
        + randomFile.getFilePointer() + "...");
    buffer.clear();
    return CommandQueue.send(new Command(Command.CMD_LOG, collector.getLogContext(), arr));
  }

  private Transporter transporter; // 传输
  private Heartbeater heartbeater; // 断线重连检测
  private int threadCount = 1; // 扫描线程数
  private int scandSeconds = 60; // 扫描文件时间间隔
  private int heartbeatSeconds = 30; // 心跳时间
  private List<Collector> collectors = new ArrayList(); // 日志收集器
  private String host; // 本机
  private String logServer; // 日志服务器IP
  private int logServerPort = 15880; // 日志服务器端口
  private String dataDir = "../data"; // 数据目录
  private ScheduledExecutorService exec; //

  private static final String KEY_THREAD_COUNT = "logmonitor.threadcount";
  private static final String KEY_SCAN_SECONDS = "logmonitor.scanseconds";
  private static final String KEY_SERVER = "logmonitor.server";
  private static final String KEY_HOST = "logmonitor.host";
  private static final String KEY_HEARTBEAT_SECONDS = "logmonitor.heartbeatseconds";

  private static final String PREFIX_COLLECTOR = "logmonitor.collector.";

  private static final String KEY_COLLECTOR_FILE = "logmonitor.collector.{0}.file";
  private static final String KEY_COLLECTOR_NEWLINE_PATTERN = "logmonitor.collector.{0}.newlinepattern";
  private static final String KEY_COLLECTOR_FILTER = "logmonitor.collector.{0}.filter";
  private static final String KEY_COLLECTOR_FILTER_WORD = "logmonitor.collector.{0}.filter.word";
  private static final String KEY_COLLECTOR_FILTER_PATTERN = "logmonitor.collector.{0}.filter.pattern";
  private static final String KEY_COLLECTOR_LOGCONTEXT = "logmonitor.collector.{0}.logcontext";
}
