package com.lz.snappy.big.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MySpringMVCFilter implements Filter{
	
	private String charset;
	private boolean foreEncoding;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.charset = filterConfig.getInitParameter("charset");
		this.foreEncoding = Boolean.getBoolean(filterConfig.getInitParameter("foreEncoding"));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("过滤器进行过滤！");
		request.setCharacterEncoding(charset);
		System.out.println("进行字符集的设置！");
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
