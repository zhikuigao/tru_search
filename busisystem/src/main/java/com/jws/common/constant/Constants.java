package com.jws.common.constant;


import java.text.SimpleDateFormat;
import java.util.regex.Pattern;


/**
 * 系统常量
 * @author ljw	
 *
 */
public class Constants {
	private Constants() {
		
	}

	public static final Pattern EMAIL_ADDRESS    = Pattern.compile(
	        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	        "\\@" +
	        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	        "(" +
	            "\\." +
	            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	        ")+"
	    );
	
	public static final Pattern PHONE    = Pattern.compile(
			 "^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|70)\\d{8}$"
	    );		
	
	public static final SimpleDateFormat  df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat  dfi = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat  sdfm = new SimpleDateFormat("yyyy-MM");
	
	public static final String CALL_SOURCE = "busiSystem";
	public static final String CALL_TO_USER = "userSystem";
	public static final String CALL_TO_APP = "appSystem";
	public static final String CALL_TO_SEARCH = "searchSystem";
	//上传DB文件夹名称
	public static final String MATE_RECORD_PATH = "record";
	//用户反馈图片对应文件夹名称
	public static final String MATE_UPLOAD_PATH = "upload";
	//搜索地图分享缩略图对应文件夹名称
	public static final String MATE_SHARE_PATH = "shareThumbnail";
	
	//个人头像存放位置
	public static final String USER_HEAD_PHOTO = "headPhoto";
	
	// 搜索文件目录
	public static final String MATE_SEARCH_FILE_PATH = "search";
	
	public static final String  GIT_LAB="gitLab";
	public static final String  RED_MINE="redMine";
	
	//请求设备
	public static final String  DEVICE_PC="pc";
	public static final String  DEVICE_ANDROID="android";
	public static final String  DEVICE_IOS="ios";
	
	//pic 
	public static final String  PIC_PNG=".png";
	
	//成功
	public static final int  RESULT_CODE_SUCCESS=0;
	public static final int  RESULT_CODE_FAIL=1;
	//其他公共返回code
	// 业务系统100
	public static final String  RESULT_CODE_100 = "The request parameter is empty";
	public static final String  RESULT_CODE_101 = "Missing request parameters";
	public static final String  RESULT_CODE_102 = "Error busiBlock";
	public static final String  SAVE_SEARCHMAPS_EXCEPTION = "Save searchMaps exception";
	public static final String  QUERY_SEARCHMAPS_HISTORY_EXCEPTION = "Query history of searchMaps exception";
	public static final String  QUERY_SEARCHMAPS_DATA_EXCEPTION = "Query data of searchMaps exception";
	public static final String  MORE_SEARCHMAPS_DATA_EXCEPTION = "Query more data of searchMaps exception";
	public static final String  QUERY_SIMILAT_KEYWORD_EXCEPTION = "Query similar keyword exception";
	public static final String  ADD_SEARCHSOURCE_EXCEPTION = "Add search source exception";
	public static final String  DELETE_SEARCHSOURCE_EXCEPTION = "Delete search source exception";
	public static final String  QUERY_SEARCHSOURCE_EXCEPTION = "Query search source exception";
	public static final String  FLAG_MAP_EXCEPTION = "Flag data of searchMaps exception";
	public static final String  MAP_RECOMMEND_EXCEPTION = "Recommend keyword of old searchMaps exception";
	public static final String  ADD_SECOND_NODE_EXCEPTION = "Add second node of searchMaps exception";
	/**busiCode 有误*/
	public static final String  RESULT_CODE_103 = "Error busiCode";
	/** 调用子系统出错 */
	public static final int  RESULT_CODE_104 = 104;
	public static final String  RESULT_CODE_105 = "Json format is incorrect";
	public static final String  RESULT_CODE_106 = "Program exception";
	public static final String  RESULT_CODE_107 = "The conversion file failed";
	public static final String  RESULT_CODE_108 = "Save stream file exception";
	public static final String  RESULT_CODE_109 = "Security verification does not pass";
	/** 未配置busicode对应的路径 */
	public static final int  RESULT_CODE_110 = 110;
	/** 请求参数有误 */
	public static final int  RESULT_CODE_111 = 111;
	/** token已过期，重新登录 */
	public static final int  RESULT_CODE_112 = 112;	
	
	//用户系统1001
	/**	 缺少请求参数	 */
	public static final int  RESULT_CODE_1001 = 1001;
	/**	 账号格式有误,请输入正确的邮箱或手机号	 */
	public static final int  RESULT_CODE_1002 = 1002;
	/** 上传文件异常*/
	public static final int  RESULT_CODE_1005 = 1005;
	/** 图片数量过多*/
	public static final int  RESULT_CODE_1006 = 1006;

	//应用系统2001
	public static final int RESULT_CODE_APP_LACK_PARAM = 2001;
	
	//搜索系统3001
//	public static final int RESULT_CODE_SEARCH_LACK_PARAM = 3001;
	public static final int RESULT_CODE_SEARCH_SAVE_FILE_FAIELD = 3002;
	public static final int  RESULT_CODE_LACK_PARAM = 3003;
	public static final int  RESULT_CODE_SECURITY_VERIFY_FAILED = 3004;
	public static final String  ERROE_MAPID = "MapId is incorrect";
	public static final String  ERROR_USERID = "UserId is incorrect";
	public static final String  ERROR_OPERATE = "The value of operate is incorrect";
	public static final String  ERROR_CONFIG_TYPE = "The value of type is incorrect";
	
	// 小美翻墙搜索失败
	public static final String FAILE_GOOGLE_SEARCH = "Over wall search failed";
	// 保存搜索条件失败
	public static final int QUERY_MAP_FAILED = 3102;
	// 保存搜索的查看记录失败
	public static final int RESULT_CODE_SAVE_VIEWED_FAILED = 3201;
	// 查询用户搜索历史失败
	public static final int RESULT_CODE_QUERY_KEY_HISTORY = 3301;
	// 查询搜索条件对应的所有链接失败
	public static final int RESULT_CODE_QUERY_VIEWED_HISTORY = 3401;
	// 保存搜索记录文件失败
	public static final int RESULT_CODE_SAVE_FILE_FAILED = 3501;
	/** 保存配置参数有误	 */
	public static final String SAVE_CONFIG_ERROR_PARAMTER = "保存配置接口参数有误";
	/** 重复的搜索源 */
	public static final int DUPLICATE_SEARCH_SOURCE_CODE = 504000;
	/** 重复的搜索源 */
	public static final String DUPLICATE_SEARCH_SOURCE = "Define duplicate search sources";
	
	public static final String  ERROR_MAPS_DATA_ID = "The dataId of map is incorrect";
	
	public static final String  SENCOD_NODE_EXIST = "The second node has exist";
	
	//第三方应用接入
	public static final int URL_ERROR = 4001;
	public static final int GET_BODY_FAIL = 4002;
}
