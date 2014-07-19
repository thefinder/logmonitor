/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-pipeline
 * 文件名：	TokenLog.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-26 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.pipeline.log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.hd123.rumba.commons.lang.Assert;

/**
 * 结构化日志消息
 * 
 * @author zhangyanbo
 * 
 */
public class TokenLog {

  private Map<String, String> values = new HashMap();

  public TokenLog() {
  }

  public TokenLog(TokenLog source) {
    this.values.putAll(source.values);
  }

  public void setValue(String token, String value) {
    Assert.assertArgumentNotNull(token, "token");

    if (value != null)
      values.put(token, value);
    else
      values.remove(token);
  }

  public String getValue(String token) {
    Assert.assertArgumentNotNull(token, "token");

    return values.get(token);
  }

  public Map<String, String> getValues() {
    return Collections.synchronizedMap(values);
  }
}
