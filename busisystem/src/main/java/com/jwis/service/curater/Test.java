package com.jwis.service.curater;

import java.io.File;

import javax.ws.rs.core.Application;

import com.jws.common.constant.ConfigConstants;

public class Test {

 public static void main(String[] args) {

	//String rootPath = Application.class.getClass().getResource("/").getFile().toString();
	String newPath = ConfigConstants.zookeeper_URL   + "service.json";
	//System.out.println(rootPath);
 	//String newPath = rootPath   + "service.json";
 	File file = new File(newPath);
 	if(file.exists()){
 		ZookeeperServiceAutoRegist regist = new ZookeeperServiceAutoRegist();
     	regist.registServices(file);
 	}else
 	System.out.println("11");
	
	
}
} 
