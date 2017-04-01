package com.jws.app.operater.serviceBlock.search.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.Module;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jws.app.operater.data.SearchConfigMapper;
import com.jws.app.operater.data.SearchKeyMapper;
import com.jws.app.operater.data.SearchMapShareMapper;
import com.jws.app.operater.data.SearchMapsMapper;
import com.jws.app.operater.data.SearchViewedMapper;
import com.jws.app.operater.data.SourceMatesearchMapper;
import com.jws.app.operater.data.SystemTimeMapper;
import com.jws.app.operater.model.SearchDefined;
import com.jws.app.operater.model.SearchInfoAll;
import com.jws.app.operater.model.SearchMapRecommend;
import com.jws.app.operater.model.SearchMapShare;
import com.jws.app.operater.model.SearchMaps;
import com.jws.app.operater.model.SearchMapsData;
import com.jws.app.operater.model.SearchMapsKeyword;
import com.jws.app.operater.model.SearchMapsKeywordNode;
import com.jws.app.operater.model.SearchSource;
import com.jws.app.operater.model.SearchViewedBy;
import com.jws.app.operater.model.SourceMatesearch;
import com.jws.app.operater.model.UserConfig;
import com.jws.app.operater.service.BlockEntryService;
import com.jws.app.operater.service.impl.InterfaceInvoking;
import com.jws.common.constant.BlockBusiCodeConstant;
import com.jws.common.constant.ConfigConstants;
import com.jws.common.constant.Constants;
import com.jws.common.util.FileUtil;
import com.jws.common.util.JiveGlobe;
import com.jws.common.util.JsonUtil;
import com.jws.common.util.Levenshtein;
import com.jws.common.util.ResultPackaging;

@Service("searchService")
public class SearchServiceImpl implements BlockEntryService {
	
	private final Logger logger = Logger.getLogger(this.getClass());
	@Resource
	private InterfaceInvoking interfaceInvoking;
	@Resource
	private SystemTimeMapper systemTimeMapper;
	@Resource
	private SearchMapsMapper searchMapsMapper;
	@Resource
	private SearchMapShareMapper searchMapShareMapper;
	@Resource
	private SourceMatesearchMapper sourceMatesearchMapper;
	@Resource
	private SearchConfigMapper searchConfigMapper;
	@Resource
	private SearchKeyMapper searchKeyMapper;
	@Resource
	private SearchViewedMapper searchViewedMapper;
	
	/**
	 * 根据busiCode分方法入口
	 * @throws Exception 
	 */
	@Override
	public JSONObject entry(JSONObject paramJson) throws Exception {
		JSONObject returnObject = new JSONObject();
		//调用子系统
		if(StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.SAVE_SEARCH_SHARE)){
			//保存分享记录
			returnObject = saveSearchShare(paramJson);
		}else if(StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.QUERY_SEARCH_SHARES)){
			//查询分享记录
			returnObject = querySearchShares(paramJson);
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.SAVE_CONFIG)) {
			//保存配置
			returnObject = saveConfig(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.QUERY_CONFIG)) {
			//查询配置
			returnObject = queryConfig(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.INTERNAL_SEARCH)) {
			//小美内部搜索接口
			returnObject = internalSearch(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.GOOGLE_DADAS)) {
			//小美翻墙搜索
			returnObject = requestGoogleDatas(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.ADDMAPS_SEARCH)) {
			//添加搜索地图
			returnObject = addMapsSearch(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.QUERYHISTORY_MAPS)) {
			//查询搜索地图历史记录
			returnObject = queryHistoryMaps(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.QUERYHISTORY_MAPSDATAS)) {
			//查询单个searchMap
			returnObject = querySearchMapsDatas(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.QUERQUERY_MAPKEYWORDDATAS)) {
			//查询searchMap关键字下更多详情接口
			returnObject = querySearchMapKeywordDatas(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.QUERQUERY_SEARCHLIKEKEYWORD)) {
			//查询keyword相似关键字
			returnObject = querySearchLikeKeyword(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.ADD_MATE_SOURCE)) {
			//增加自定义源
			returnObject = addSearchSource(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.DELETE_MATE_SOURCE)) {
			//删除自定义源
			returnObject = delSearchSource(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.QUERY_MATE_SOURCE)) {
			//查询所有源
			returnObject = querySearchSource(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.QUERY_MAPSDATA_NODES)) {
			//查询搜索地图
			returnObject = querySearchMapsDataNodes(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.ADD_MAPSNODES)) {
			//二级详情添加
			returnObject = addMapNodes(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.FLAG_MAP_DATA)) {
			//给地图内的文章加标记
			returnObject = flagMapData(paramJson);	
		}else if (StringUtils.equals(paramJson.optString("busiCode"), BlockBusiCodeConstant.OLD_MAP_RECOMMEND)) {
			//搜索关键词时旧地图推荐
			returnObject = oldMapRecommend(paramJson);	
		}
		else {
			//找不到对应的业务方法
			returnObject = ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_103, null);
		}
		return returnObject;
	}
	/**
	 * 搜索关键词时旧地图推荐
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private JSONObject oldMapRecommend(JSONObject requestJson){
		if (!requestJson.has("userId")|| !requestJson.has("keyword") ) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		try {
			String userId = requestJson.getString("userId");
			String keyword = requestJson.getString("keyword");
			//获取同一关键字历史map
			List<SearchMapRecommend> list = searchMapsMapper.queryOldMapBySameKey(userId, keyword);
			if (null == list || list.size() == 0) {
				return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, null);
			}
			//获取每个关键字下，标记的文章数
			HashMap<String, Object> idMap = new HashMap<>();
			Boolean flagged = false;
			for (int i = 0; i < list.size(); i++) {
				SearchMapRecommend map = list.get(i);
				map.setDataCount(searchMapsMapper.countFlaggedMapData(map.getKeywordId()));
				if (map.getDataCount()>0) {
					flagged = true;
				}
				//过滤同一个map下搜索多次此关键字，并叠加标记文章
				if (idMap.containsKey(map.getId())) {
					SearchMapRecommend oldMap = (SearchMapRecommend) idMap.get(map.getId());
					//同一个地图下不同关键字标记文章数叠加
					oldMap.setDataCount(map.getDataCount()+oldMap.getDataCount());				
				}else{
					idMap.put(map.getId(), map);
				}			
			}
			//如果不存在标记的文章，直接返回最新的map
			if (!flagged) {
				return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, list.get(0));
			}
			list = new ArrayList<>();
			Iterator iter = idMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				list.add((SearchMapRecommend) entry.getValue());
			}
			//排序
			Collections.sort(list);
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, list.get(0));
		} catch (Exception e) {
			logger.error("Recommend keyword of old searchMaps exception:"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.MAP_RECOMMEND_EXCEPTION, null);
		}
	}
	/**
	 * 给地图内的文章加标记
	 * @return
	 */
	private JSONObject flagMapData(JSONObject requestJson){
		if (!requestJson.has("id")|| !requestJson.has("flag") ) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		SearchMapsData data = new SearchMapsData();
		try {
			data.setId(requestJson.getInt("id"));
			data.setKeepFlag(requestJson.getString("flag"));
			int result = searchMapsMapper.updateSearchDataFlag(data);
			if (result>0) {
				return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, null);
			}
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.ERROR_MAPS_DATA_ID, null);
		} catch (Exception e) {
			logger.error("Flag data of searchMaps exception:"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.FLAG_MAP_EXCEPTION, null);
		}
	}
	/**
	 * 获取小美内部搜索推荐结果
	 * @return
	 */
	private JSONObject internalSearch(JSONObject requestJson){
		if (!requestJson.has("searchKey")|| StringUtils.isEmpty(requestJson.optString("searchKey")) 
				|| !requestJson.has("number") || !requestJson.has("userId")) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		int number = Integer.valueOf(requestJson.optString("number"));
		List<SearchViewedBy> list= this.querySearchRecommend(requestJson);
		if (list.size()>number) {
			list = list.subList(0, number);
		}
		return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_SUCCESS, null, list);
	}
	private List<SearchViewedBy> querySearchRecommend(JSONObject param) {
		List<SearchViewedBy> list= new ArrayList<>();
		if (param.has("userId")) {
			String userId = param.getString("userId");
			List<SearchInfoAll> views = searchViewedMapper.queryRecommendSearchHis(userId);
			if (null != views && views.size()>0) {
				int size = views.size();
				for (int i = 0; i < size; i++) {
					//获取相似度
					Float simvalue=Levenshtein.levenshtein(param.getString("searchKey"), views.get(i).getTitleTxt());
					if (simvalue>0.2) {
						SearchViewedBy view = new SearchViewedBy(views.get(i),simvalue);
						list.add(view);
					}
				}
			}
		}
		if (list.size()==0) {
			List<SearchInfoAll> views = searchViewedMapper.queryRecommendSearchHis(null);
			if (null != views && views.size()>0) {
				int size = views.size();
				for (int i = 0; i < size; i++) {
					//获取相似度
					Float simvalue=Levenshtein.levenshtein(param.getString("searchKey"), views.get(i).getTitleTxt());
					if (simvalue>0.2) {
						SearchViewedBy view = new SearchViewedBy(views.get(i),simvalue);
						list.add(view);
					}
				}
			}
		}
		if (list.size()>0) {
			Collections.sort(list);
		}
		return list;
	}
	/**
	 * 用户配置搜索源
	 * @return
	 */
	private JSONObject queryConfig(JSONObject requestJson){
		if (!requestJson.has("userId")) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		String userId = requestJson.optString("userId");
		//查询系统搜索源
		List<SearchSource>  searchSource = searchConfigMapper.queryAllSource(userId);
		//查询用户自定义搜索源
		List<SearchDefined> searchDefined = searchConfigMapper.queryUserDefined(userId);
		//模块
		List<Module> module = searchConfigMapper.queryModule(userId);
		HashMap<String, Object> resultMap = new HashMap<>();
		resultMap.put("searchSource", null==searchSource?"":searchSource);
		resultMap.put("searchDefined", null==searchDefined?"":searchDefined);
		resultMap.put("module", null==module?"":module);
		return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_SUCCESS, null, resultMap);
	}
	
	private JSONObject addMapsSearch(JSONObject requestJson){
		if ( !requestJson.has("mapId")|| !requestJson.has("userId")
				|| (!requestJson.has("keyword") && (!requestJson.has("keywordId") || !requestJson.has("title")  
						||!requestJson.has("url") || !requestJson.has("sourceSearch")))) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		try {
				String mapsId =  requestJson.getString("mapId");
				String userId = requestJson.getString("userId");
				String keyword = requestJson.getString("keyword");
				String keywordId = requestJson.getString("keywordId");
				String title = requestJson.getString("title");
				String url = requestJson.getString("url");
				String sourceSearch = requestJson.optString("sourceSearch");
				int count  = this.searchMapsMapper.querySearchMapsIstrue(mapsId);
				if(count<1){
					//查询rodmap表是否已经存在了这个搜索记录，若没有就添加
					SearchMaps mp = new SearchMaps();
					mp.setCreateTime(systemTimeMapper.getSystemTime());
					mp.setFirstKeyword(keyword);
					mp.setId(mapsId);
					mp.setUserId(userId);
					this.searchMapsMapper.insert(mp);
				}
				int countKeyWorldId  = this.searchMapsMapper.querySearchMapKeywordsIstrue(keywordId);
				//若有重复的keyword则不需要添加进数据库，若没重复的需要到数据库查询是否有相同关键字的keywrod
				if(countKeyWorldId<1){
					SearchMapsKeyword sm = this.searchMapsMapper.querySearchMapsSameKeyWords(mapsId);
					if(!JiveGlobe.isEmpty(sm) && sm.getKeyword().toString().equals(keyword)){
							keywordId = sm.getId();
					}else{
						SearchMapsKeyword sd = new SearchMapsKeyword();
						sd.setId(keywordId);
						sd.setCreateTime(systemTimeMapper.getSystemTime());
						sd.setKeyword(keyword);
						sd.setMapId(mapsId);
						sd.setSource(sourceSearch);
						this.searchMapsMapper.insertSearchMapsKeyword(sd);
					}
				}
				//添加详情
				int countData = this.searchMapsMapper.queryDataIsTrue(keywordId, url);
				if(countData<1){
					SearchMapsData sa = new SearchMapsData();
					sa.setCreateTime(systemTimeMapper.getSystemTime());
					sa.setKeywordId(keywordId);
					sa.setUrl(url);
					sa.setTitle(title);
					this.searchMapsMapper.insertSearchMapsData(sa);
				}
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, null);
		} catch (Exception e) {
			logger.error("Save searchMaps exception:"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.SAVE_SEARCHMAPS_EXCEPTION, null);
		}
	}
	/**
	 * 用户配置搜索源
	 * @return
	 */
	private JSONObject saveConfig(JSONObject requestJson){
		if (!requestJson.has("userId") || !requestJson.has("type")|| !requestJson.has("operate")
				|| (!requestJson.has("id") && (!requestJson.has("name") || !requestJson.has("url"))) ) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		String userId = requestJson.getString("userId");
		String type = requestJson.getString("type");
		String operate = requestJson.getString("operate");
		if (!StringUtils.equals("1", requestJson.optString("type")) && 
				!StringUtils.equals("2", requestJson.optString("type"))&&
				!StringUtils.equals("3", requestJson.optString("type"))) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.ERROR_CONFIG_TYPE, null);
		}
		//type 1 模块 2搜索 
		if (StringUtils.equals("1", type) || StringUtils.equals("2", type)) {
			if (!StringUtils.equals("subscribe", requestJson.optString("operate")) && 
					!StringUtils.equals("cancel", requestJson.optString("operate"))) {
				return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.ERROR_OPERATE, null);
			}
			return saveSubscribe(type, operate, userId, requestJson.getString("id"));
		}
		if(StringUtils.equals("3", type)){
			//删除
			if (requestJson.has("id") && StringUtils.equals("delete", requestJson.getString("operate"))) {
				searchConfigMapper.deleteUserDefined(requestJson.getString("id"));
			}else if (requestJson.has("name") && requestJson.has("url") && StringUtils.equals("add", requestJson.getString("operate"))) {
				//新增
				String name= requestJson.getString("name");
				String url= requestJson.getString("url");
				//检索重复数据
				int count = searchConfigMapper.countUserDefined(name, url, userId);
				if (count >0 ) {
					return ResultPackaging.dealReturnObject(Constants.DUPLICATE_SEARCH_SOURCE_CODE, Constants.DUPLICATE_SEARCH_SOURCE, null);
				}
				SearchDefined defined = new SearchDefined();
				defined.setId(UUID.randomUUID().toString().replaceAll("-", ""));
				defined.setName(name);
				defined.setUrl(url);
				defined.setUserId(userId);
				searchConfigMapper.addUserDefined(defined);
				return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_SUCCESS, null, defined);
			}else{
				return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.SAVE_CONFIG_ERROR_PARAMTER, null);
			}
		}
		return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_SUCCESS, null, null);
	}
	private JSONObject saveSubscribe(String type,String operate, String userId,String id){
		if (StringUtils.equals("subscribe", operate)) {
			//订阅
			UserConfig config = new UserConfig();
			config.setUserId(userId);
			config.setSourceId(id);
			config.setType(type);
			List<UserConfig> oldConfigs = searchConfigMapper.queryUserConfig(config);
			if (null != oldConfigs && oldConfigs.size()>0) {
				config.setFlag("1");
				searchConfigMapper.updateUserConfig(config);
			}else{
				config.setId(UUID.randomUUID().toString().replaceAll("-", ""));
				searchConfigMapper.addUserConfig(config);
			}
		}
		if (StringUtils.equals("cancel", operate)) {
			//取消订阅
			UserConfig config = new UserConfig();
			config.setUserId(userId);
			config.setSourceId(id);
			config.setFlag("2");
			config.setType(type);
			searchConfigMapper.updateUserConfig(config);
		}
		return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_SUCCESS, null, null);
	}
	private JSONObject queryHistoryMaps(JSONObject requestJson){
		if (!requestJson.has("userId") ||!requestJson.has("key") ||!requestJson.has("page") ||!requestJson.has("pageNum")) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		try {
			    JSONObject json = new JSONObject();
			    int count = 0;
				String userId = requestJson.getString("userId");
				String key = requestJson.getString("key");
				Integer page = requestJson.optInt("page");
				Integer pageNum = requestJson.optInt("pageNum");
				if(JiveGlobe.isEmpty(key)){
					key = null;
				}else{
					key = "%" + key +"%";
				}
				List<SearchMaps> queryHistoryMaps = this.searchMapsMapper.queryHistoryMaps(userId,key,page*pageNum,pageNum);
				if(!JiveGlobe.isEmpty(queryHistoryMaps)){
					List<SearchMaps> queryMaps = new ArrayList<SearchMaps>();
					count = this.searchMapsMapper.queryHistoryMapsCount(userId,key,page,pageNum);
					for(int i=0;i<queryHistoryMaps.size();i++){
						SearchMaps sm = queryHistoryMaps.get(i);
						String mapId = sm.getId();
						//查询出所有的关键字
						List<String> labellist = new ArrayList<String>();
						List<SearchMapsKeyword> sk = this.searchMapsMapper.querySearchMapsKeyWords(mapId);
						for(int j=0;j<sk.size();j++){
							SearchMapsKeyword sd = sk.get(j);
							if(j<10){	
								labellist.add(sd.getKeyword());
							}
						}
						sm.setLabel(labellist);
						queryMaps.add(sm);
					}
					queryHistoryMaps = queryMaps;
				}
				json = JsonUtil.addJsonObject("total", count, json);
				json = JsonUtil.addJsonObject("list", queryHistoryMaps, json);
				json = JsonUtil.addJsonObject("page", page, json);
				//更新用户现使用的版本信息
//				if (page==0 && StringUtils.isNotEmpty(requestJson.optString("mateVersion")) && StringUtils.isNotEmpty(requestJson.optString("device"))) {
//					userService.synUserMateVersion(requestJson.optString("mateVersion"), requestJson.optString("device"), userId);
//				} 
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, json);
		} catch (Exception e) {
			logger.error("Query history of searchMaps exception"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.QUERY_SEARCHMAPS_HISTORY_EXCEPTION, null);
		}
	}
	
	private JSONObject querySearchMapsDatas(JSONObject requestJson){
		if (!requestJson.has("id")) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		JSONObject jsonObject = new JSONObject();
		try {
			String id=  requestJson.getString("id");
			SearchMaps searchMaps = this.searchMapsMapper.queryMaps(id);
			if(!JiveGlobe.isEmpty(searchMaps)){
				jsonObject = JsonUtil.addJsonObject("maps", searchMaps, jsonObject);
				List<SearchMapsKeyword> queryKeyWords = this.searchMapsMapper.queryKeyWords(id);
				if(!JiveGlobe.isEmpty(queryKeyWords)){
					jsonObject = JsonUtil.addJsonObject("mapKeywords", queryKeyWords, jsonObject);
					String keywordId = "";
					List<SearchMapsData> dataList = new ArrayList<SearchMapsData>();
					//迭代keyword数据，得到详情
					List<SearchMapsData> queryDatas = new ArrayList<SearchMapsData>();
					for(int i=0;i<queryKeyWords.size();i++){
						SearchMapsKeyword sw = queryKeyWords.get(i);
						keywordId = sw.getId();
						 queryDatas = this.searchMapsMapper.queryDatas(keywordId);
						if(!JiveGlobe.isEmpty(queryDatas)){
							dataList.addAll(queryDatas);
						}
					}
					//组装数据
					jsonObject = JsonUtil.addJsonObject("mapDatas", dataList, jsonObject);
				}
			}
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, jsonObject);
		} catch (Exception e) {
			logger.error("Query data of searchMaps exception"+e.getMessage());
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.QUERY_SEARCHMAPS_DATA_EXCEPTION, null);
		}
	}
	
	private JSONObject querySearchMapsDataNodes(JSONObject requestJson){
		if (!requestJson.has("id")) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		JSONObject jsonObject = new JSONObject();
		try {
			//新版search地图
			String id=  requestJson.getString("id");
			SearchMaps searchMaps = this.searchMapsMapper.queryMaps(id);
			List<SearchMapsKeyword> queryKeyWords = this.searchMapsMapper.queryKeyWords(id);
			if(!JiveGlobe.isEmpty(searchMaps)){
				jsonObject = JsonUtil.addJsonObject("maps", searchMaps, jsonObject);
				if(!JiveGlobe.isEmpty(queryKeyWords)){
					jsonObject = JsonUtil.addJsonObject("mapKeywords", queryKeyWords, jsonObject);
					String keywordId = "";
					List<SearchMapsData> dataList = new ArrayList<SearchMapsData>();
					//迭代keyword数据，得到详情
					List<SearchMapsData> queryDatas = new ArrayList<SearchMapsData>();
					for(int i=0;i<queryKeyWords.size();i++){
						SearchMapsKeyword sw = queryKeyWords.get(i);
						keywordId = sw.getId();
						 queryDatas = this.searchMapsMapper.queryDatas(keywordId);
						//查询二级url
						if(!JiveGlobe.isEmpty(queryDatas)){
							for(int j=0;j<queryDatas.size();j++){
								SearchMapsData sd = new SearchMapsData();
								sd = queryDatas.get(j);
								//查询是否有二级url
								 List<SearchMapsKeywordNode> nodelist= this.searchMapsMapper.querySearchMapsKeyWordsNodes( sd.getId()+"");
								 sd.setDataNode(nodelist);
								dataList.add(sd);
							}
						}
					}
					//组装数据
					jsonObject = JsonUtil.addJsonObject("mapDatas", dataList, jsonObject);
				}
			}
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, jsonObject);
		} catch (Exception e) {
			logger.error("Query searchMaps exception:"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.QUERY_SEARCHMAPS_DATA_EXCEPTION, null);
		}
	}
	
	private JSONObject addMapNodes(JSONObject requestJson){
		if (!requestJson.has("titleId") || !requestJson.has("title")||!requestJson.has("url")	) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		try {
			SearchMapsKeywordNode sn = new SearchMapsKeywordNode();
			String titleId = requestJson.optString("titleId");
			String	 title = requestJson.optString("title");
			String	 url = requestJson.optString("url");
			sn.setCreateTime(systemTimeMapper.getSystemTime());
			sn.setTitle(title);
			sn.setTitleId(Integer.valueOf(titleId));
			sn.setUrl(url);
			int  node = this.searchMapsMapper.querySearchMapsKeyWordsNodesCount(titleId,url);
			if(node>0){
				return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.SENCOD_NODE_EXIST, null);
			}
			this.searchMapsMapper.insertNodes(sn);
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, null);
		} catch (Exception e) {
			logger.error("Add second node of searchMaps exception:"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.ADD_SECOND_NODE_EXCEPTION, null);
		}
	}
	
	private JSONObject querySearchMapKeywordDatas(JSONObject requestJson){
		if (!requestJson.has("keyWord") ||!requestJson.has("page") ||!requestJson.has("pageNum") ) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		JSONObject jsonObject = new JSONObject();
		try { 
			String keyWord = requestJson.getString("keyWord");
			int count = 0;
			Integer page = requestJson.optInt("page");
			Integer pageNum = requestJson.optInt("pageNum");
			List<SearchMapsData> queryDatas = this.searchMapsMapper.queryDatasSimple(keyWord,page*pageNum,pageNum);
			jsonObject = JsonUtil.addJsonObject("list", queryDatas, jsonObject);
			if(!JiveGlobe.isEmpty(queryDatas)){
				count = this.searchMapsMapper.queryDatasCount(keyWord);
			}
			jsonObject = JsonUtil.addJsonObject("total", count, jsonObject);
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, jsonObject);
		} catch (Exception e) {
			logger.error("Query more data of searchMaps exception"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.MORE_SEARCHMAPS_DATA_EXCEPTION, null);
		}
	}
	
	private JSONObject querySearchLikeKeyword(JSONObject requestJson){
		if (!requestJson.has("key") ||!requestJson.has("userId")  ) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		try {
			String keyWord = requestJson.getString("key");
			String userId = requestJson.getString("userId");
			List<SearchMapsKeyword>  list = this.searchMapsMapper.querySearchLikeKeyword(keyWord+"%", userId);
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, list);
		} catch (Exception e) {
			logger.error("Query similar keyword exception"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.QUERY_SIMILAT_KEYWORD_EXCEPTION, null);
		}
	}
	/**
	 * 小美翻墙搜索, 部署在香港服务器，暂时没用
	 * @param paramJson
	 * @return
	 */
	private JSONObject requestGoogleDatas(JSONObject paramJson) {
		if (!paramJson.has("start") || !paramJson.has("queryString") ) {
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		String start = paramJson.optString("start");
		String queryString = paramJson.optString("queryString");
		JSONObject json = new JSONObject();
		try {
			 HttpClient client = new HttpClient();//定义client对象
			 client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);//设置连接超时时间为2秒（连接初始化时间）
			 GetMethod method = new GetMethod(""+ConfigConstants.GOOGLESEARCH_IP+"/googleSearch/data.do?start="+start+"&queryString="+queryString+"");//访问下谷歌的首页
			 int statusCode = client.executeMethod(method);//状态，一般200为OK状态，其他情况会抛出如404,500,403等错误
			if (statusCode != 200) {
				return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.FAILE_GOOGLE_SEARCH, null);
			}
		//	System.out.println(method.getResponseBodyAsString());//输出反馈结果						    
			client.getHttpConnectionManager().closeIdleConnections(1);
		//	System.out.println(method.getResponseBodyAsString());
			json = JSONObject.fromObject(method.getResponseBodyAsString());
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_SUCCESS, null , json);
		} catch (Exception e) {
			logger.error("Over wall search interface exception:"+e.getMessage());
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, "Over wall search interface exception", null);
		}
	}
	/**
	 * 保存分享记录
	 * @param param
	 * @return
	 */
	private JSONObject saveSearchShare(JSONObject param){
		if (StringUtils.isEmpty(param.optString("userId")) || StringUtils.isEmpty(param.optString("mapId"))
				|| StringUtils.isEmpty(param.optString("shareTo")) 
				|| StringUtils.isEmpty(param.optString("fileKey")) ) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		String picName = param.optString("mapId")+(int)(Math.random()*1000)+Constants.PIC_PNG;
		String allPath = ConfigConstants.SHARE_PIC_URL+File.separator+picName;
		String httpPath = FileUtil.saveBase64Pic(param.optString("fileKey"), allPath);
		//查询对应的地图信息
		SearchMaps map = searchMapsMapper.queryMapById(param.getString("mapId"));
		if (null == map) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.ERROE_MAPID, null);
		}
		SearchMapShare share = new SearchMapShare();
		share.setUserId(param.getString("userId"));
		share.setFirstKeyword(map.getFirstKeyword());
		share.setMapId(param.getString("mapId"));
		share.setShareTo(param.getString("shareTo"));
		if (StringUtils.equals(Constants.DEVICE_PC, param.optString("device"))) {
			share.setShareDevice("1");
		}else if(StringUtils.equals(Constants.DEVICE_ANDROID, param.optString("device"))){
			share.setShareDevice("2");
		}else{
			share.setShareDevice("3");
		}
		share.setPicUrl(httpPath);
		share.setThumbnailUrl(httpPath);
		//保存分享信息
		searchMapShareMapper.insert(share);
		return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_SUCCESS, null, null);
	}
	
	private JSONObject querySearchShares(JSONObject param) throws Exception{
		if (StringUtils.isEmpty(param.optString("userId")) || StringUtils.isEmpty(param.optString("pageNum"))
				|| StringUtils.isEmpty(param.optString("page")) 
				|| (param.optInt("page")>0 && StringUtils.isEmpty(param.optString("firstTime")))) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		JSONObject result = new JSONObject();
		String userId = param.optString("userId");
		Integer page = param.optInt("page");
		List<SearchMapShare> list = new ArrayList<>();
		SearchMapShare record = new SearchMapShare();
		record.setPageNum(param.optInt("pageNum"));
		record.setUserId(userId);
		if (page<0) {
			page=0;
		}
		record.setPageFrom(page*param.optInt("pageNum"));
		Integer total = 0;
		String firstTime = param.optString("firstTime");
		Date newTime = null;
		if (page>0) {
			total = searchMapShareMapper.getTotalNumber(userId,Constants.df.parse(firstTime));
			result.put("firstTime", param.optString("firstTime"));
		}else{
			newTime = systemTimeMapper.getSystemTime();
			total = searchMapShareMapper.getTotalNumber(userId,newTime);
			result.put("firstTime", Constants.df.format(newTime));
		}
		list = searchMapShareMapper.queryShareByPage(record);
		result.put("page", ++page);
		result.put("total", total);
		if (list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				if (StringUtils.isNotEmpty(list.get(i).getThumbnailUrl())) {
					list.get(i).setThumbnailUrl(ConfigConstants.HTTP_VISIT+list.get(i).getThumbnailUrl());
				}
			}
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			String gsont = gson.toJson(list);	
			result.put("list", gsont);
		}else{
			result.put("list", list);
		}
		return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_SUCCESS, null, result);
		
	}
	
	
	private JSONObject addSearchSource(JSONObject param){
		if (StringUtils.isEmpty(param.optString("userId")) || StringUtils.isEmpty(param.optString("type")) || StringUtils.isEmpty(param.optString("url"))
				|| StringUtils.isEmpty(param.optString("name"))) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		try { 
			String name = param.getString("name");
			String url = param.getString("url");
			String type = param.getString("type");
			String userId = param.getString("userId");
			//查询出用户是否已经存在重复的源
			int count   = this.sourceMatesearchMapper.userSourceCount(name, userId);
			if(count>0){
				return ResultPackaging.dealReturnObject( Constants.DUPLICATE_SEARCH_SOURCE_CODE, Constants.DUPLICATE_SEARCH_SOURCE, null);
			}
			SourceMatesearch sm = new SourceMatesearch();
			String romString = JiveGlobe.getFromRom();
			sm.setCreateTime(systemTimeMapper.getSystemTime());
			sm.setDeleteFlag("1");
			sm.setId(romString);
			sm.setName(name);
			sm.setType(Integer.valueOf(type));
			sm.setUrl(url);
			sm.setUserId(userId);
			this.sourceMatesearchMapper.insert(sm);
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, romString);
		} catch (Exception e) {
			logger.error("Add search source exception:"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.ADD_SEARCHSOURCE_EXCEPTION, null);
		}
	}
	
	private JSONObject delSearchSource(JSONObject param){
		if (StringUtils.isEmpty(param.optString("id")) ) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		try { 
			String id = param.getString("id");
			 this.sourceMatesearchMapper.delSearchSource(id);
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, null);
		} catch (Exception e) {
			logger.error("Delete search source exception:"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.DELETE_SEARCHSOURCE_EXCEPTION, null);
		}
	}
	
	private JSONObject querySearchSource(JSONObject param){
		if (StringUtils.isEmpty(param.optString("userId")) ) {
			return ResultPackaging.dealReturnObject(Constants.RESULT_CODE_FAIL, Constants.RESULT_CODE_101, null);
		}
		try { 
			String userId = param.getString("userId");
			List<SourceMatesearch> sm = this.sourceMatesearchMapper.querySearchSource(userId);
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_SUCCESS, null, sm);
		} catch (Exception e) {
			logger.error("Query search source exception:"+e.getMessage());
			return ResultPackaging.dealReturnObject( Constants.RESULT_CODE_FAIL, Constants.QUERY_SEARCHSOURCE_EXCEPTION, null);
		}	
	}
	
	
}
