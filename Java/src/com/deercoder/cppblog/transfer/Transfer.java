package com.deercoder.cppblog.transfer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.net.MalformedURLException;
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

	private static final String KEY_GET_BLOGID = "blogid";
	private static final String KEY_GET_URL = "url";
	private static final String KEY_GET_BLOG_NAME = "blogName";

	private static class CategoryInfo{
		private static final String KEY_DESCRIPTION = "description";
		private static final String KEY_HTML_URL = "htmlUrl";
		private static final String KEY_RSS_URL = "rssUrl";
		private static final String KEY_CATELOG_TITLE = "title";
		private static final String KEY_CATELOG_ID = "categoryid";
	};

	// 提供服务的地址
	private static String USER_NAME = "xxx"; // 用户名
	private static String USER_PASSWORD = "xxx"; // 用户密码
	private static String USER_ID = ""; // 用户的ID（由程序获得）

	// XML-RPC client config setting for all usage
	XmlRpcClientConfigImpl config;
	XmlRpcClient client;

	/**
	* @param url 服务提供商的MetaBlog的地址
	*/
	public Transfer(String url){
		try {
			config =  new XmlRpcClientConfigImpl();
			client = new XmlRpcClient();
			config.setServerURL(new URL(url));
			client.setConfig(config); 
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	* 获取博客信息（注意不是所有文章），包括博客主页，名称，以及注册博客的ID
	*/
	public void fetchBlogInfo() throws Exception {
		List params = new ArrayList();
		params.add("test");
		params.add(USER_NAME);
		params.add(USER_PASSWORD);
		// 必須使用Object數組，List或者其他數組不行
		Object[] arr = (Object[])client.execute(GET_BLOGS_METHOD, params);
		for (int i = 0; i < arr.length; i++){
			// 仍然利用Map的键值映射关系，在API中告诉了对应的键，即此处的宏KEY_GET_XXX
			Map<String, String> map = (Map<String, String>) arr[i];
			String blogid = map.get(KEY_GET_BLOGID);
			if (blogid != null){
				USER_ID = blogid;
			}
			String blogurl = map.get(KEY_GET_URL);
			String blogname = map.get(KEY_GET_BLOG_NAME);
			System.out.println(blogid + "\n" + blogurl + "\n" + blogname);
			Thread.sleep(1000);
		}
	}

	/**
	* 获取博客分类信息（注意必须在fetchBlogInfo后使用，因为要似乎博客的ID，而只能先调用它获取）
	*/
	public void getCateloge() throws Exception {
		List params = new ArrayList();
		params.add(USER_ID);
		params.add(USER_NAME);
		params.add(USER_PASSWORD);

		/* 返回的值就是一个数组，存放内容为一个个键值对，每一个键值对对应着对应的信息，比如<K,V>=<title,value> */
		Object[] arr = (Object[]) client.execute(GET_CATEGORY, params);
		for (int i = 0; i < arr.length; i++){
			Map<String, String> map = (Map<String, String>)arr[i];
			String description = map.get(CategoryInfo.KEY_DESCRIPTION);
			String htmlUrl = map.get(CategoryInfo.KEY_HTML_URL);
			String rssUrl = map.get(CategoryInfo.KEY_RSS_URL);
			String categoryTitle = map.get(CategoryInfo.KEY_CATELOG_TITLE);
			String categoryId = map.get(CategoryInfo.KEY_CATELOG_ID);
			System.out.println("Category Description: " + description + "\n"
								  +"Category HTML Url: " + htmlUrl + "\n"
								  +"Category RSS Url: " + rssUrl + "\n"
								  +"Category Title: "+ categoryTitle + "\n"
								  +"Category ID: " + categoryId + "\n");
			Thread.sleep(1000); //避免频繁的服务器请求，休息1s
		}
	}

	/**
	* 获取近期发布Post的内容（标题，时间和内容），可指定数量
	* @param postNum 近期发布博客数量
	*/
	public void getRecentPosts(int postNum) throws Exception {
		List params = new ArrayList();
		params.add(USER_ID);
		params.add(USER_NAME);
		params.add(USER_PASSWORD);
		params.add(postNum);

		Object[] recentPosts = (Object[]) client.execute(GET_RECENT_POST, params);
		for (int i = 0; i < recentPosts.length; i++) {
			System.out.println();
			String postInfo = readFromPost((Map<String, Object>)recentPosts[i]);
			System.out.println(postInfo);
		}
	}

	/**
	* 获取Post的所需内容（标题，时间和内容），并以String返回HTML形式
	* @param postId 发布博客文章的id
	*/
	public String getOnePost(String postId) throws Exception {
		// 这个是方法需要的参数，参数必须按照方法要求的顺序添加，详细的可以在服务提供页查看
		String saveString = "";
		List params = new ArrayList();
		params.add(postId);
		params.add(USER_NAME);
		params.add(USER_PASSWORD);

		Map<String, Object> result = (Map<String, Object>) client.execute(
				GET_POST, params);
		return readFromPost(result);
	}

	/**
	* 获取Post的所需内容（标题，时间和内容），并以String返回HTML形式
	* @param result 一个post对象（这里只读取我所需要的键值对，即标题，时间和内容，当然还可以读取其他内容）
	*/
	public String readFromPost(Map<String, Object> result){
		String saveString = "";
		String title = getPostTitle(result);
		if (title != null){
			System.out.println(title);
			saveString += title + "<br/>"; // 文章标题，并以HTML形式存放
		}

		Date date = getPostDate(result);
		if (date != null) {
			String dateString = getPostTime(date);
			System.out.println(dateString);
			saveString += dateString + "<br/>";
		}

		String article = getPostArticle(result);
		if (article != null) {
			System.out.println(article);
			saveString += article + "<br/>";
		}
		return saveString;
	}

	/**
	* 获取Post的标题（即每一篇博客的标题）
	* @param result 发送一次getPost请求后接收的结果
	*/
	public void saveToHTML(String savePath, String content){
		try {
			PrintWriter out = new PrintWriter(savePath);
			out.println(content);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	* 获取Post的标题（即每一篇博客的标题）
	* @param result 发送一次getPost请求后接收的结果
	*/
	public String getPostTitle(Map<String, Object> result){
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
	public String getPostArticle(Map<String, Object> result){
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
	public Date getPostDate(Map<String, Object> result){
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
	public String getPostTime(Date date) {
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
