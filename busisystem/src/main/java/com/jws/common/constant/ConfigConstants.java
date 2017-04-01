package com.jws.common.constant;


import java.util.Properties;

import com.jws.common.util.FileOperation;


/**
 * 系统常量
 * @author ljw	
 *
 */
public class ConfigConstants {
	private ConfigConstants() {
	}

	//读取常量配置表
	public static Properties properties=FileOperation.readConfigProperties("dataConfig.properties");	
	/** 用户子系统 */
	public static final String URL_USER_SYSTEM = "http://localhost:8080/usersystem/entry/userBlockEntry.do?paramObject=";	
	/** 应用子系统 */
	public static final String URL_APP_SYSTEM = "http://localhost:8080/appsystem/entry/appBlockEntry.do?paramObject=";
	/** 安全验证key */
	public static final String SECURITY_KEY = properties.getProperty("Security.key");	
	/** 保存流文件临时路径 */
	public static final String FILE_SAVE_URL_TEMP = properties.getProperty("FileSave.url.temp");
	/** 保存流文件正式路径 */
	public static final String FILE_SAVE_URL_FORMAL = properties.getProperty("FileSave.url.formal");
	
	public static final String UPLOAD_FILE_URL_FORMAL = properties.getProperty("Upload.file.url");
	
	/** hdfs文件路径 record */
//	public static final String FILE_RECORD_URL = properties.getProperty("Hdfs.record.url");
	
	public static final String FILE_SERVER = properties.getProperty("File.server");
	
	public static final String QQ_CALLBACK_IP = "http://127.3.133.130:10030";
	
	public static final String QQ_CONSUMER_KEY ="101291757";
	
	public static final String PLUG_IN_GITLAB = properties.getProperty("Plug.in.gitLab");
	
	public static final String PLUG_IN_REDMINE = properties.getProperty("Plug.in.redMine");
	
	public static final String MC_APP_ID = properties.getProperty("MC.app.id");
	
	public static final Integer LOGIN_EFFECTIVE_TIME = 28;
	
	public static final String FIRST_FOLDER_NAME = properties.getProperty("First.folder.name");
	
	public static final String SHARE_PIC_URL = properties.getProperty("Share.pic.url");
	public static final String zookeeper_URL = properties.getProperty("zookeeper.url");
	
	public static final String GOOGLESEARCH_IP = properties.getProperty("GoogleSearch.ip");
	
	public static final String HTTP_VISIT = properties.getProperty("Http.visit");
	
}
