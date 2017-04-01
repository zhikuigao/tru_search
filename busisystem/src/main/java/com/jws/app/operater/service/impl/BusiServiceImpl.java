package com.jws.app.operater.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.jws.app.operater.data.SystemTimeMapper;
import com.jws.app.operater.service.BlockEntryService;
import com.jws.app.operater.service.BusiService;
import com.jws.app.operater.service.SysBusiLogService;
import com.jws.app.operater.serviceBlock.plugIn.service.PlugInService;
import com.jws.app.operater.serviceBlock.user.service.UserTokenService;
import com.jws.common.constant.BlockConstatnt;
import com.jws.common.constant.ConfigConstants;
import com.jws.common.constant.Constants;
import com.jws.common.util.FileUtil;
import com.jws.common.util.MD5Util;
import com.jws.common.util.ResultPackaging;

@Service("busiService")
public class BusiServiceImpl implements BusiService{
	private final Logger logger = Logger.getLogger(this.getClass());
	@Resource
	private BlockEntryService  userService;
	@Resource
	private BlockEntryService  appService;
	@Resource
	private BlockEntryService  searchService;
	@Resource
	private PlugInService plugInService;
	@Resource
	private SysBusiLogService sysBusiLogService;
	@Resource
	private SystemTimeMapper systemTimeMapper;
	@Resource
	private UserTokenService userTokenService;


	@Override
	public JSONObject commonEntry(HttpServletRequest request) {
		//1.读取参数值		
		JSONObject returnObject= new JSONObject();
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (Exception e) {
			logger.error("Encoding exception:"+e.getMessage());
		}
		String param = request.getParameter("paramObject");
		if (StringUtils.isEmpty(param)) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_100, null);
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = JSONObject.fromObject(param);
		} catch (Exception e) {
			logger.error("Json format is incorrect:"+e.getMessage());
			return   ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_105, null);
		}
		//来源 pc/android/ios
		String  device = request.getHeader("device");
		jsonObject.put("device", null==device?"":device);
//		jsonObject.put("mateVersion", null==request.getHeader("version")?"":request.getHeader("version"));
		//2.记录接口调用日志
//		SysBusiLog log  = sysBusiLogService.addCallLog(jsonObject);	
		//处理默认值
//		if (StringUtils.isEmpty(language)) {
//			language = "ZH";
//		}
		if (StringUtils.isEmpty(device)) {
			device = Constants.DEVICE_PC;
		}
		if (!jsonObject.has("userId")) {
			jsonObject.put("userId", null==request.getHeader("userId")?"":request.getHeader("userId"));
		}
		jsonObject.put("device", device);
		//参数验证
		if ( !jsonObject.has("busiBlock") || !jsonObject.has("time") || !jsonObject.has("Md5Str")
				|| !jsonObject.has("busiCode")) {
			returnObject =  ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
//			sysBusiLogService.updateCallLog(returnObject.toString(), log, jsonObject);
			return  returnObject;
		}
		//3.安全控制
		JSONObject securityObject = securityVerification(jsonObject);
		if (null != securityObject) {
//			sysBusiLogService.updateCallLog(securityObject.toString(), log, jsonObject);
			return  securityObject;
		}
		//4.进入子模块业务处理
		try {
			if (StringUtils.equals(jsonObject.get("busiBlock").toString(), BlockConstatnt.USER_BLOCK)) {
				//用户模块
				returnObject = userService.entry(jsonObject);
			}else if (StringUtils.equals(jsonObject.get("busiBlock").toString(), BlockConstatnt.APP_BLOCK)) {
				//应用模块
				returnObject = appService.entry(jsonObject);
			} else if (StringUtils.equals(jsonObject.get("busiBlock").toString(), BlockConstatnt.SEARCH_BLOCK)) {
				//搜索模块
				returnObject = searchService.entry(jsonObject);
			} else if (StringUtils.equals(jsonObject.get("busiBlock").toString(), BlockConstatnt.PLUG_IN_BLOCK)) {
				//第三方接入
				returnObject = plugInService.entry(jsonObject);
			} else {
				//找不到对应的模块
				returnObject = ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_102, null);
//				sysBusiLogService.updateCallLog(returnObject.toString(), log, jsonObject);
				return  returnObject;
			}	
		} catch (Exception e) {
			logger.error("Program exception:"+e.getMessage());
			returnObject = ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_106, null);
		}			
		//5.更新接口返回日志
//		sysBusiLogService.updateCallLog(returnObject.toString(), log, jsonObject);
		//6.返回结果
		return returnObject;
	}
	
	@Override
	public JSONObject commonFileEntry(HttpServletRequest request) {
		JSONObject returnObject = new JSONObject();
		String filePath = ConfigConstants.FILE_SAVE_URL_TEMP+File.separator+UUID.randomUUID().toString().replaceAll("-", "");
		File repositoryFile = new File(filePath);
		try {
			if (!repositoryFile.exists()) {
				repositoryFile.mkdirs();
			}
		} catch (Exception e) {
			logger.error("Create folder exception:"+e);
		}
		//将请求信息流上传到该路径下
		List<?> items = null;
		try {
			items = FileUtil.parseRequest(repositoryFile, request);
		} catch (FileUploadException e) {
			logger.error("The conversion file exception："+e.getMessage());
			FileUtil.deleteFile(repositoryFile.getParentFile());
			return  ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_107, null);
		}
		//保存文件
		JSONObject  paramJson = new JSONObject();
		try {
			paramJson = FileUtil.saveStream(repositoryFile, items);
			if (!paramJson.has("busiBlock")) {
				JSONObject otherObject = JSONObject.fromObject(request.getParameter("paramObject"));
				paramJson.putAll(otherObject);
			}
		} catch (Exception e) {
			logger.error("Save stream file exception："+e.getMessage());
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_108, null);
		}
		//来源 pc/android/ios
		String  device = request.getHeader("device");
		paramJson.put("device", null==device?"":device);
		//2.记录接口调用日志
//		SysBusiLog log  = sysBusiLogService.addCallLog(paramJson);
		if (StringUtils.isEmpty(device)) {
			device = "pc";
		}
//		paramJson.put("language", language);
		paramJson.put("device", device);
		//验证
		if (paramJson.isEmpty() || !paramJson.has("busiBlock") || !paramJson.has("time")
				|| !paramJson.has("Md5Str")|| !paramJson.has("busiCode")) {
			returnObject =  ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
			FileUtil.deleteTempFile(paramJson);
//			sysBusiLogService.updateCallLog(returnObject.toString(), log, paramJson);
			return  returnObject;
		}
		
		//3.安全控制
		JSONObject securityObject = securityVerification(paramJson);
		if (null != securityObject) {
			FileUtil.deleteTempFile(paramJson);
//			sysBusiLogService.updateCallLog(securityObject.toString(), log, paramJson);
			return  securityObject;
		}
		//4.进入子模块业务处理
		try {
			if (StringUtils.equals(paramJson.get("busiBlock").toString(), BlockConstatnt.USER_BLOCK)) {
				//用户模块
				returnObject = userService.entry(paramJson);
			} else if (StringUtils.equals(paramJson.get("busiBlock").toString(), BlockConstatnt.SEARCH_BLOCK)) {
				//搜索模块
				returnObject = searchService.entry(paramJson);
			} else {
				//找不到对应的模块
				returnObject = ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_102, null);
//				sysBusiLogService.updateCallLog(returnObject.toString(), log, paramJson);
				return  returnObject;
			}	
		} catch (Exception e) {
			FileUtil.deleteTempFile(paramJson);
			logger.error("Program exception:"+e.getMessage());
			returnObject = ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_106, null);
		}			
		//5.更新接口返回日志
//		sysBusiLogService.updateCallLog(returnObject.toString(), log, paramJson);
		//6.返回结果
		return returnObject;
	}

	/**
	 * 安全验证
	 * @param streamMap
	 * @return
	 */
	private JSONObject  securityVerification(JSONObject  streamMap){
		String time = streamMap.get("time").toString();
		String Md5Str = streamMap.get("Md5Str").toString();
		String key = ConfigConstants.SECURITY_KEY;
		String newMd5Str = MD5Util.getMD5String(key+time);
		if (!StringUtils.equals(Md5Str, newMd5Str)) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_109, null);
		}
		return  null;
	}
	
//	public static void main(String[] args) {
//		String requestUrl = " http://localhost:8080/usersystem/entry/userBlockEntry.do?paramObject=";
//		String  key = requestUrl.substring(requestUrl.lastIndexOf("/")+1, requestUrl.lastIndexOf("."));
//		System.out.println(key);
//	}

	@Override
	public long getSysTime() {
		Date data = systemTimeMapper.getSystemTime();
		return data.getTime();
	}


}
