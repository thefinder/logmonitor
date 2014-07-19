/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	LogClientHandler.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-14 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangyanbo
 * 
 */
public class LogClientHandler extends SimpleChannelHandler {
  private final Logger logger = LoggerFactory.getLogger(LogClientHandler.class);
  private boolean debug = logger.isDebugEnabled();

  private Transporter transporter;

  public LogClientHandler(Transporter transporter) {
    this.transporter = transporter;
  }

  @Override
  public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    logger.info("connected");
    try {
      Command log;
      while ((log = CommandQueue.take()) != null) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

        byte[] cmd = log.getCmd().getBytes();
        buffer.writeInt(cmd.length);
        buffer.writeBytes(cmd);

        if (log.getContext() != null) {
          byte[] context = log.getContext().getBytes("utf-8");
          buffer.writeInt(context.length);
          buffer.writeBytes(context);
        } else
          buffer.writeInt(0);

        if (log.getBody() != null) {
          buffer.writeInt(log.getBody().length);
          buffer.writeBytes(log.getBody());
        } else
          buffer.writeInt(0);

        if (debug)
          logger.debug("send " + log.getCmd() + " " + log.getContext());
        ChannelFuture f = e.getChannel().write(buffer);
        int c = 0;
        while (f.isDone() == false && c++ < 10) {
          Thread.sleep(100);
        }
        if (f.isDone() == false) {
          if (debug)
            logger.debug("send failed");
          e.getChannel().close();
          break;
        }
      }
    } catch (InterruptedException ex) {
      transporter.setState(TransportState.broken);
      logger.error("", ex);
    } catch (Exception ex2) {
      transporter.setState(TransportState.broken);
      logger.error("", ex2);
    }
  }

  public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
    logger.error("连接出错：" + e.getCause());
    transporter.setState(TransportState.broken);
    e.getChannel().close();
  }

}
