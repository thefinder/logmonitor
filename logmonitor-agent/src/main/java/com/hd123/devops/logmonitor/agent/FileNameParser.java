/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	FileNameParser.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-16 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhangyanbo
 * 
 */
public class FileNameParser {

  public static String getRealName(String s) {
    if (s == null)
      return null;

    while (true) {
      int fromIndex = s.indexOf("${");
      if (fromIndex < 0)
        return s;

      int toIndex = s.indexOf("}", fromIndex);
      if (toIndex < 0)
        return s;

      String token = s.substring(fromIndex + 2, toIndex);
      String param = null;
      int paramIndex = token.indexOf(":");
      if (paramIndex > 0) {
        param = token.substring(paramIndex + 1);
        token = token.substring(0, paramIndex);
      }
      s = s.substring(0, fromIndex) + replaceToken(token, param) + s.substring(toIndex + 1);
    }
  }

  private static String replaceToken(String token, String param) {
    if ("date".equals(token)) {
      if (param == null)
        param = "yyyy-MM-dd";
      return new SimpleDateFormat(param).format(new Date());
    } else
      throw new RuntimeException("占位符无法识别：" + token);
  }

}
