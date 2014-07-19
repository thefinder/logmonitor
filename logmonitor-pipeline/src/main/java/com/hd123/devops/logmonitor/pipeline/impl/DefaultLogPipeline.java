/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	DefaultLogPipeline.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.hd123.devops.logmonitor.pipeline.LogEvent;
import com.hd123.devops.logmonitor.pipeline.LogHandler;
import com.hd123.devops.logmonitor.pipeline.LogPipeline;

/**
 * @author zhangyanbo
 * 
 */
public class DefaultLogPipeline implements LogPipeline {
  private String name = "default";
  private volatile DefaultLogHandlerContext head;
  private volatile DefaultLogHandlerContext tail;
  private final Map<String, DefaultLogHandlerContext> nameCtx = new HashMap();

  public DefaultLogPipeline() {
  }

  public DefaultLogPipeline(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public synchronized LogPipeline addFirst(String name, LogHandler handler) {
    if (nameCtx.isEmpty()) {
      init(name, handler);
    } else {
      checkDuplicateName(name);
      DefaultLogHandlerContext oldHead = head;
      DefaultLogHandlerContext newHead = new DefaultLogHandlerContext(name, handler, this, null,
          oldHead);
      oldHead.prev = head;
      head = newHead;
      nameCtx.put(name, newHead);
    }
    return this;
  }

  @Override
  public synchronized LogPipeline addLast(String name, LogHandler handler) {
    if (nameCtx.isEmpty()) {
      init(name, handler);
    } else {
      checkDuplicateName(name);
      DefaultLogHandlerContext oldTail = tail;
      DefaultLogHandlerContext newTail = new DefaultLogHandlerContext(name, handler, this, oldTail,
          null);
      oldTail.next = newTail;
      tail = newTail;
      nameCtx.put(name, newTail);
    }
    return this;
  }

  @Override
  public synchronized void remove(LogHandler handler) {
    DefaultLogHandlerContext ctx = getContext(handler);
    if (ctx == null)
      return;

    remove(ctx);
  }

  @Override
  public synchronized LogHandler remove(String name) {
    DefaultLogHandlerContext ctx = nameCtx.get(name);
    if (ctx == null)
      return null;
    remove(ctx);
    return ctx.getHandler();
  }

  @Override
  public synchronized LogHandler removeFirst() {
    if (head == null)
      return null;

    DefaultLogHandlerContext oldHead = head;
    if (oldHead.next == null) {
      head = tail = null;
      nameCtx.clear();
    } else {
      oldHead.next.prev = null;
      head = oldHead.next;
      nameCtx.remove(oldHead.getName());
    }
    return oldHead.getHandler();
  }

  @Override
  public synchronized LogHandler removeLast() {
    if (tail == null)
      return null;

    DefaultLogHandlerContext oldTail = tail;
    if (oldTail.prev == null) {
      head = tail = null;
      nameCtx.clear();
    } else {
      oldTail.prev.next = null;
      tail = oldTail.prev;
      nameCtx.remove(oldTail.getName());
    }
    return oldTail.getHandler();
  }

  @Override
  public LogHandler getFirst() {
    DefaultLogHandlerContext head = this.head;
    if (head == null)
      return null;
    return head.getHandler();
  }

  @Override
  public LogHandler getLast() {
    DefaultLogHandlerContext tail = this.tail;
    if (tail == null)
      return null;
    return tail.getHandler();
  }

  @Override
  public LogHandler get(String name) {
    DefaultLogHandlerContext ctx = nameCtx.get(name);
    if (ctx == null)
      return null;
    return ctx.getHandler();
  }

  @Override
  public Set<String> getNames() {
    return nameCtx.keySet();
  }

  @Override
  public void handleEvent(LogEvent e) throws Exception {
    DefaultLogHandlerContext head = this.head;
    if (head == null)
      return;

    head.getHandler().invoke(head, e);
  }

  private void init(String name, LogHandler handler) {
    DefaultLogHandlerContext ctx = new DefaultLogHandlerContext(name, handler, this, null, null);
    head = tail = ctx;
    nameCtx.clear();
    nameCtx.put(name, ctx);
  }

  private void checkDuplicateName(String name) {
    if (nameCtx.containsKey(name))
      throw new IllegalArgumentException("添加了重复名称 " + name + " 的处理器。");
  }

  private DefaultLogHandlerContext remove(DefaultLogHandlerContext ctx) {
    if (ctx == head)
      removeFirst();
    else if (ctx == tail)
      removeLast();
    else {
      DefaultLogHandlerContext prev = ctx.prev;
      DefaultLogHandlerContext next = ctx.next;
      prev.next = next;
      next.prev = prev;
      nameCtx.remove(ctx.getName());
    }
    return ctx;
  }

  private DefaultLogHandlerContext getContext(LogHandler handler) {
    if (handler == null || nameCtx.isEmpty())
      return null;

    for (DefaultLogHandlerContext ctx = head; ctx != null; ctx = ctx.next) {
      if (ctx.getHandler() == handler)
        return ctx;
    }
    return null;
  }
}
