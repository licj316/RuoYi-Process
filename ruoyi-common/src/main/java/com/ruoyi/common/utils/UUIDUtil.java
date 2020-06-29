package com.ruoyi.common.utils;

import java.util.UUID;

/**
*@author tracywindy
*@createDate 2010-3-6 下午01:52:08
**/
public class UUIDUtil
{
  public static String getUUID()
  {
	  String s = UUID.randomUUID().toString();
	  return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
  }
}
