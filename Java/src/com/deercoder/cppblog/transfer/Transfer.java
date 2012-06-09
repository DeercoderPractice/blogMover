package com.deercoder.cppblog.transfer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.net.URL;
import java.io.*;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Transfer {

	private final static String FORMAT_DATETIME = "yyyy-MM-dd   HH:mm:ss ";
	/*
	 * define the method which the service provider offers to get your blog
	 * content
	 */
	/*
	 * blogger.deletePost 
	 * blogger.getUsersBlogs 
	 * metaWeblog.editPost
	 * metaWeblog.getCategories 
	 * metaWeblog.getPost 
	 * metaWeblog.getRecentPosts
	 * metaWeblog.newMediaObject 
	 * metaWeblog.newPost
	 */

	private static String DELETE_BLOGS_METHOD = "blogger.deletePost";
	private static String GET_BLOGS_METHOD = "blogger.getUsersBlogs";
	private static String GET_CATEGORY = "metaWeblog.getCategories";
	private static String GET_POST="metaWeblog.getPost";
	private static String GET_RECENT_POST="metaWeblog.getRecentPosts";

	/* 下面内容为服务提供商提供的Post结构体的键值，即变量名，最终拿到的数据为
	 * 键值对，根据此键值来获取数据，如Post结构体中一个变量为String description
	 * 则此处Key即可description，根据此Key利用Map的get()可以获得对应的值
	 * Post的具体信息：
	 *  dateTime	dateCreated - Required when posting.
	 *	string	description - Required when posting.
	 *	string	title - Required when posting.
	 *	array of string	categories (optional)
	 *	struct Enclosure	enclosure (optional)
	 *	string	link (optional)
	 *	string	permalink (optional)
	 *	any	postid (optional)
	 *	struct Source	source (optional)
	 *	string	userid (optional)
	 * */
	private static final String KEY_TITLE = "title";
	private static final String KEY_ARTICLE = "description";
	private static final String KEY_DATE = "dateCreated";
	
	// 提供服务的地址
	private static String URL = "http://www.cppblog.com/deercoder/services/metaweblog.aspx";
	private static String POST_ID = "174774"; // 文章id
	private static String USER_NAME = "xxx"; // 用户名
	private static String USER_PASSWORD = "xxx"; // 用户密码

	public static void main(String[] args) throws Exception {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(URL));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config); 
		// 这个是方法需要的参数，参数必须按照方法要求的顺序添加，详细的可以在服务提供页查看
		List params = new ArrayList();
		params.add(POST_ID);
		params.add(USER_NAME);
		params.add(USER_PASSWORD);
				
		Map<String, Object> result = (Map<String, Object>) client.execute(
				GET_POST, params);
		PrintWriter out = new PrintWriter("/media/Code/a.html");
		
		String title = getPostTitle(result);
		if (title != null){
			System.out.println(title);
			out.println(title + "<br/>"); // 文章标题，并以HTML形式存放
		}
		
		
		Date date = getPostDate(result);	
		if (date != null) {
			String dateString = getPostTime(date);
			System.out.println(dateString);
			out.println(dateString + "<br/>");
		}
		
		String article = getPostArticle(result);
		if (article != null) {
			System.out.println(article);
			out.println(article + "<br/>");
		}
		
		out.close();
	}
	
	/** 
	* 获取Post的标题（即每一篇博客的标题）
	* @param result 发送一次getPost请求后接收的结果
	*/ 
	public static String getPostTitle(Map<String, Object> result){
		if (result == null){
			return null;
		}else{
			String title = (String)result.get(KEY_TITLE);
			if (title == null){
				return null;
			} else{
				return title;
			}	
		}
	}
	
	/** 
	* 获取Post的内容（即每一篇博客的内容）
	* @param result 发送一次getPost请求后接收的结果
	*/ 
	public static String getPostArticle(Map<String, Object> result){
		if (result == null){
			return null;
		}else{
			String title = (String)result.get(KEY_ARTICLE);
			if (title == null){
				return null;
			} else{
				return title;
			}	
		}
	}
	
	/** 
	* 获取Post的时间（即每一篇博客发布的时间）
	* @param result 发送一次getPost请求后接收的结果
	*/ 
	public static Date getPostDate(Map<String, Object> result){
		if (result == null) {
			return null;
		} else {
			Date date = (Date) result.get(KEY_DATE);
			if (date == null) {
				return null;
			} else {
				return date;
			}
		}
	}
	
	/** 
	* 转换成所需要的时间格式
	* @param date 获取的文章发布时间
	*/ 
	public static String getPostTime(Date date) {
		if (date == null) {
			return null;
		}
		String tmpString = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				FORMAT_DATETIME);
		try {
			tmpString = simpleDateFormat.format(date);
		} catch (Exception e) {
		}
		return tmpString;
	}

}
