/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-server
 * 文件名：	Acceptor.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-15 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.server;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.devops.logmonitor.pipeline.LogPipeline;
import com.hd123.devops.logmonitor.pipeline.impl.DefaultLogEvent;
import com.hd123.devops.logmonitor.pipeline.log.RawLog;

/**
 * @author zhangyanbo
 * 
 */
public class Acceptor {
  private final Logger logger = LoggerFactory.getLogger(Acceptor.class);

  public Acceptor(int port, LogPipeline pipeline) {
    this.port = port;
    this.pipeline = pipeline;
    bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
        Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
    bootstrap.setOption("child.tcpNoDelay", true);
    bootstrap.setOption("child.keepAlive", true);
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      @Override
      public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new CommandDecoder());
        pipeline.addLast("handler", new LogServerHandler(Acceptor.this));
        return pipeline;
      }
    });
  }

  public void start() {
    bootstrap.bind(new InetSocketAddress(port));
    logger.info("日志服务启动完成，端口：" + port);
  }

  public void stop() {
    if (bootstrap != null)
      bootstrap.releaseExternalResources();
  }

  public void log(MessageEvent e, Command cmd) throws Exception {
    RawLog log = new RawLog();
    log.setContext(cmd.getContext());
    log.setBody(cmd.getBody());
    pipeline.handleEvent(new DefaultLogEvent(log));
  }

  public void ping(MessageEvent e, Command cmd) throws Exception {
    String remoteServer = getRemoteServer(e);
    pingStatus.put(remoteServer, new Date());
  }

  private String getRemoteServer(MessageEvent e) {
    String s = e.getRemoteAddress().toString();
    s = s.replaceAll("/", "");
    int index = s.indexOf(":");
    if (index > 0)
      s = s.substring(0, index);
    return s;
  }

  private ServerBootstrap bootstrap;
  private int port;
  private LogPipeline pipeline;

  public static final Map<String, Date> pingStatus = new ConcurrentHashMap();
}
