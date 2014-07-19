/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	Transporter.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-14 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangyanbo
 * 
 */
public class Transporter {
  private final Logger logger = LoggerFactory.getLogger(Transporter.class);

  private ClientBootstrap bootstrap;
  private String server;
  private int port;
  private Runnable callback;
  private TransportState state = TransportState.initial;

  public Transporter(String server, int port) {
    this.server = server;
    this.port = port;
  }

  public void start(final Runnable callback) {
    this.callback = callback;
    connect();
  }

  public void stop() {
    if (bootstrap != null)
      bootstrap.releaseExternalResources();
  }

  private void connect() {
    setState(TransportState.connecting);
    bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
        Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
    // 设置一个处理服务端消息和各种消息事件的类(Handler)
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      @Override
      public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("handler", new LogClientHandler(Transporter.this));
        return pipeline;
      }
    });
    final ChannelFuture f = bootstrap.connect(new InetSocketAddress(server, port));
    f.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
          setState(TransportState.connected);
          callback.run();
        } else {
          setState(TransportState.broken);
          f.getChannel().close();
        }
      }
    });
  }

  public void reconnect() {
    logger.info("重新连接 " + server + ":" + port + " ...");
    try {
      bootstrap.releaseExternalResources();
    } catch (Exception e) {
    }
    connect();
  }

  public TransportState getState() {
    return state;
  }

  public synchronized void setState(TransportState state) {
    this.state = state;
  }

  public String getServer() {
    return server;
  }

  public int getPort() {
    return port;
  }

}
