/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	logmonitor-agent
 * 文件名：	MainTest.java
 * 模块说明：	
 * 修改历史：
 * 2014-6-10 - zhangyanbo - 创建。
 */
package com.hd123.devops.logmonitor.agent;

import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangyanbo
 * 
 */
public class RegexTest {
  private static final Logger logger = LoggerFactory.getLogger(RegexTest.class);

  @Test
  public void regex() {
    Pattern p = Pattern.compile(".*\\s+ERROR\\s+.*");
    logger.info(String.valueOf(p.matcher("11 043 ERROR dasf").matches()));
    
    p = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}.*");
    logger.info(String.valueOf(p.matcher("2014-05-03 ").matches()));
    logger.info(String.valueOf(p.matcher("com.hd123 ").matches()));
  }
}
