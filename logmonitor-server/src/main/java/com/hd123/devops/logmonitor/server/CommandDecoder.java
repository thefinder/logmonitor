/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-server
 * 文件名：	LogDecoder.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-15 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * @author zhangyanbo
 * 
 */
public class CommandDecoder extends FrameDecoder {

  @Override
  protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
      throws Exception {
    if (buffer.readableBytes() < 4)
      return null;

    buffer.markReaderIndex();

    // 读取cmd
    int cmdLength = buffer.readInt();
    if (buffer.readableBytes() < cmdLength) {
      buffer.resetReaderIndex();
      return null;
    }
    String cmd = new String(buffer.readBytes(cmdLength).array());

    // 读取context
    if (buffer.readableBytes() < 4) {
      buffer.resetReaderIndex();
      return null;
    }
    int contextLength = buffer.readInt();
    if (buffer.readableBytes() < contextLength) {
      buffer.resetReaderIndex();
      return null;
    }
    String context = "";
    if (contextLength > 0)
      context = new String(buffer.readBytes(contextLength).array());

    // 读取content
    if (buffer.readableBytes() < 4) {
      buffer.resetReaderIndex();
      return null;
    }
    int bodyLength = buffer.readInt();
    if (buffer.readableBytes() < bodyLength) {
      buffer.resetReaderIndex();
      return null;
    }
    byte[] body = null;
    if (bodyLength > 0)
      body = buffer.readBytes(bodyLength).array();

    return new Command(cmd, context, body);
  }
}
