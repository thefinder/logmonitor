/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-server
 * 文件名：	LogServerHandler.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-14 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangyanbo
 * 
 */
public class LogServerHandler extends SimpleChannelHandler {
  private final Logger logger = LoggerFactory.getLogger(LogServerHandler.class);
  private boolean debug = logger.isDebugEnabled();

  private Acceptor acceptor;

  public LogServerHandler(Acceptor acceptor) {
    super();
    this.acceptor = acceptor;
  }

  public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    logger.info("agent connected: " + e.getChannel().getRemoteAddress());
  }

  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
    try {
      Command cmd = (Command) e.getMessage();
      if (debug)
        logger.debug("received: " + e.getRemoteAddress() + ", " + cmd.getCmd() + " "
            + cmd.getContext());
      if (Command.CMD_LOG.equals(cmd.getCmd()))
        acceptor.log(e, cmd);
      else if (Command.CMD_PING.equals(cmd.getCmd()))
        acceptor.ping(e, cmd);
      else
        logger.error("无法识别的命令: " + cmd.getCmd());
    } catch (Exception ex) {
      logger.error("", ex);
    }
  }

  public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
    logger.error("agent error: " + e.getChannel().getRemoteAddress().toString(), e.getCause());
    e.getChannel().close();
  }

  public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    logger.info("agent disconnected: " + e.getChannel().getRemoteAddress());
  }

}
