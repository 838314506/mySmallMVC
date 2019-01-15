package com.lz.snappy.big.util;

/**
 * 此类所有的工具类
 * @author lzz
 *
 */
public class CommenUtil {
	
	/**
	 * 将类名的首字母小写
	 * @param name
	 * @return
	 */
	public static String toLowerCaseFirstWord(String name) {
		char[] chs = name.toCharArray();
		//在ascii表中的规律，小写字母的所在位置比大写字母的位置大于32
		chs[0] += 32;
		return String.valueOf(chs);
	}
	
	/**
	 * 判断requestMapping中的value是否包含/,返回一个正确的字符串
	 * @param url
	 * @return
	 */
	public static String ifContainsSprit(String url) {
		if(url.startsWith("/")) {
			return url;
		}else {
			return "/" + url;
		}
	}

}
