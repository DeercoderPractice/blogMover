package com.deercoder.cppblog.transfer;

public class MoveTest {

	/**
	 * These information is variable according to your service provider or blog address
	 * 
	 */
	private static String URL = "http://www.cppblog.com/deercoder/services/metaweblog.aspx";
	private static String POST_ID = "174774"; // 文章id
	private final static String SAVE_PATH = "/media/Code/post.html";
	
	/** 
	* 主程序，用于抓取博客内容和其他功能的函数组合，测试
	*/ 
	public static void main(String[] args) throws Exception {
		// XML-RPC config setting
		Transfer tr = new Transfer(URL);
		
		//get and save Post
		String str = tr.getOnePost(POST_ID);
		tr.saveToHTML(SAVE_PATH, str);
		
	}

}
