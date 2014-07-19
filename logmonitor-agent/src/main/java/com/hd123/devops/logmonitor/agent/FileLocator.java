/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	FileIndexer.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-16 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zhangyanbo
 * 
 */
public class FileLocator {

  private String namePattern;

  // 记录上次读取位置
  private long lastPosition = 0L;
  private String lastHeadLine;
  private long lastModified = 0L;

  private File file;

  private boolean positionChanged = false;

  public long getLastPosition() {
    return lastPosition;
  }

  public void setLastPosition(long lastPosition) {
    this.lastPosition = lastPosition;
  }

  public String getLastHeadLine() {
    return lastHeadLine;
  }

  public void setLastHeadLine(String lastHeadLine) {
    this.lastHeadLine = lastHeadLine;
  }

  public long getLastModified() {
    return lastModified;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  public String getNamePattern() {
    return namePattern;
  }

  public void setNamePattern(String namePattern) {
    this.namePattern = namePattern;
  }

  public File getFile() {
    if (file == null) {
      if (namePattern == null)
        throw new RuntimeException("未指定文件名格式");
      file = new File(FileNameParser.getRealName(namePattern.trim()));
    }
    return file;
  }

  public boolean exists() {
    return getFile().exists();
  }

  public String getFileName() {
    return getFile().getAbsolutePath();
  }

  public boolean modified() {
    return getFile().lastModified() != lastModified;
  }

  public boolean headLineChanged(String headLine) {
    if (headLine == null)
      return false;

    return md5(headLine).equals(lastHeadLine) == false;
  }

  public void resetPosition() {
    lastPosition = 0L;
    lastHeadLine = "";
    lastModified = 0L;
    positionChanged = true;
  }

  public void movePosition(long modified, long position, String headLine) {
    this.lastModified = modified;
    this.lastPosition = position;
    this.lastHeadLine = md5(headLine);
    positionChanged = true;
  }

  public boolean tryRolling() {
    String oldName = getFileName();
    file = null;
    String newName = getFileName();
    if (oldName.equals(newName) == false) {
      resetPosition();
      return true;
    } else
      return false;
  }

  private String md5(String key) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(key.getBytes());
      byte b[] = md.digest();
      int i;
      StringBuffer buf = new StringBuffer(32);
      for (byte aB : b) {
        i = aB;
        if (i < 0) {
          i += 256;
        }
        if (i < 16) {
          buf.append("0");
        }
        buf.append(Integer.toHexString(i));
      }
      return buf.toString().substring(8, 24);
    } catch (NoSuchAlgorithmException e) {
      return key;
    }
  }

  public boolean isPositionChanged() {
    return positionChanged;
  }

  public void setPositionChanged(boolean positionChanged) {
    this.positionChanged = positionChanged;
  }

}
