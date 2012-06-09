package com.deercoder.cppblog.transfer;

/**
 * This class is used to test our library which provide various functions
 * like fetching blogs, sending blogs etc.
 */
public class MoveTest {

	/**
	 * These members are variables according to your service provider or blog address
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

		// 获取博客信息，包括博客标题，博客ID，博客的网址URL
		tr.fetchBlogInfo();
		// 获取博客的分类信息，包括分类名称，描述，HTML网址，RSS订阅网址，分类ID
		tr.getCateloge();
		// 获取近期发布的博客，返回值为HTML形式（参数为指定的文章数量）
		tr.getRecentPosts(10);

		// 获取某一篇blog并保存位本地的HTML文件（blogID和存储位置在上面确定，可自行修改）
		fetchAndSave(tr);
	}

	/**
	 * 根据传入博客的ID，抓取一条博客的相关信息，如标题，内容，时间等，并保存到指定路径下
	 * @param tr 博客抓取类库对象
	 */
	public static void fetchAndSave(Transfer tr) throws Exception {
		//get and save Post
		String str = tr.getOnePost(POST_ID);
		tr.saveToHTML(SAVE_PATH, str);
	}

}
