package com.lwj.status;

import com.lwj.utils.PropertiesUtil;

import java.io.InputStream;
import java.util.ResourceBundle;

/**
 * Created by liwj0 on 2017/7/14.
 */
public class CommonVariable {

    public static String oauthServer = PropertiesUtil.getStringByKey("oauthServer.url");
    public static int clientId = Integer.parseInt(PropertiesUtil.getStringByKey("oauthServer.clientId"));
    public static String clientSecret = PropertiesUtil.getStringByKey("oauthServer.clientSecret");

    public static String wspServer = PropertiesUtil.getStringByKey("wspServer");
    public static Long tokenOutPeriod = Long.parseLong(PropertiesUtil.getStringByKey("tokenOutPeriod"));

    public static double EXCELLENT_PERCENT = Double.parseDouble(PropertiesUtil.getStringByKey("EXCELLENT_PERCENT"));

    public static String apiServer = PropertiesUtil.getStringByKey("apiServer");

    public static String VERSION = PropertiesUtil.getStringByKey("VERSION");
}
