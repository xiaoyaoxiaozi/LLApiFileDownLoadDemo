package com.lianlianpay.utils;

import java.io.File;

public class Test {
	
	public static void main(String[] args) { 
		
		/*String urlPath = "http://localhost:8080/firstserver/files/test.zip";
		String destFilePath = "C:\\Users\\admin\\Desktop\\杂\\临时仓库\\test.zip";*/
		String urlPath = "http://localhost:8080//bptb_pay_file-JAVA-UTF-8/log/log2.zip";
		String destFilePath = "D:\\temp\\201310102000003524\\log2.zip";
		
		int threadCount = 1; 
		/*
	JFileDownloader downloader = new JFileDownloader(urlPath, destFilePath, threadCount);
//或者：
		 */	JFileDownloader downloader = new JFileDownloader()
		 .setUrlPath(urlPath) 
		 .setDestFilePath(destFilePath) 
		 .setThreadCount(threadCount) 
		 .setFileDownloadListener(new JFileDownloadListener() { // 设置进度监听器
			 public void downloadProgress(int progress, double speed, long remainTime) {
				 System.out.println("文件已下载：" + progress + "%，下载速度为：" + speed + "kb/s，剩余所需时间：" + remainTime + "毫秒");
			 }
			 @Override
			 public void downloadCompleted(File file, long downloadTime) {
				 System.out.println("文件：" + file.getName() + "下载完成，用时：" + downloadTime + "毫秒");
			 }
		 });
		 
		 try {
			 downloader.startDownload(); // 开始下载
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	}

}

