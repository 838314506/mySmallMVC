package com.lz.snappy.big.dispatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.lz.snappy.big.anno.MyController;
import com.lz.snappy.big.anno.MyRequestMapping;
import com.lz.snappy.big.util.CommenUtil;

public class MyDispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -6197120300629000155L;

	// 用于读取配置文件application.properties中的内容
	private Properties prop = new Properties();
	// 用于存放指定扫描包下的所有类
	private List<String> classNames = new ArrayList<>();
	// 用于将扫描到的类放入ioc容器当中
	private Map<String, Object> iocIns = new HashMap<>();
	// 用于存放handlerMapping的集合
	private Map<String, Method> handlerMapping = new HashMap<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		StringBuffer requestURL = req.getRequestURL();
		System.out.println(requestURL);
		Method method ;
		for(Entry<String,Method> entry : handlerMapping.entrySet()) {
			String url = entry.getKey();
			if(!requestURL.toString().contains(url)) {
				System.out.println("没有找到");
				resp.getWriter().write("404 Not Found!");
				return;
			}
			method = entry.getValue();
		}
//		method.invoke(obj, args)
	}

	/**
	 * 初始化方法，用于加载配置，扫描包结构等操作
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("=========进入初始化方法，开始初始化dispatcherServlet");
		// 1、读取配置文件
		doLoadConfig(config.getInitParameter("contextConfigLocation"));
		System.out.println("=========配置文件加载完毕！");
		// 2、扫描包，将所有的类的类名放入一个集合中
		doScanPackage(prop.getProperty("scanPackage"));
		System.out.println("=========包级扫描完毕！");
		// 3、通过类名，反射为实例，并且放入ioc容器中管理
		doInstance();
		System.out.println("=========ioc容器初始化完毕！屌了吧！");
		// 4、生成handlerMapping,对应handler和method
		initHandlerMapping();
		System.out.println("=========请求处理映射生成完毕，确实屌。。。");
		

	}

	/**
	 * 读取配置文件中的内容
	 */
	protected void doLoadConfig(String fileName) {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);) {
			prop.load(is);
		} catch (IOException e) {
			System.out.println("==========加载配置文件出现问题========" + e.getMessage());
		}
	}

	/**
	 * 将指定包下的所类的路径名放入集合内
	 * 
	 * @param packageName
	 */
	protected void doScanPackage(String packageName) {
		URL url = this.getClass().getClassLoader()
				.getResource("/" + packageName.replaceAll("\\.", "/"));
		File files = new File(url.getFile());
		for (File file : files.listFiles()) {
			if (file.isDirectory()) {
				doScanPackage(packageName + "." + file.getName());
			} else {
				String className = packageName + "." + file.getName().replace(".class", "");
				classNames.add(className);
			}
		}
	}

	/**
	 * 将类进行实例化，将放入ioc中进行管理，key的首字母小写
	 */
	protected void doInstance() {
		if (classNames == null) {
			return;
		}
		for (String str : classNames) {
			try {
				if (str.contains("CommenUtil")) {
					continue;
				}
				Class<?> clazz = Class.forName(str);
				if(clazz.isAnnotation()) {
					continue;
				}
				iocIns.put(CommenUtil.toLowerCaseFirstWord(clazz.getSimpleName()),
						clazz.newInstance());
			} catch (Exception e) {
				
				System.out.println("=====通过类名实例化类的时候出现问题=====" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void initHandlerMapping() {
		if (iocIns == null) {
			return;
		}
		for (Entry<String, Object> entry : iocIns.entrySet()) {
			Class<? extends Object> clazz = entry.getValue().getClass();
			if (!clazz.isAnnotationPresent(MyController.class)) {
				continue;
			}
			if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
				MyRequestMapping anno = clazz.getAnnotation(MyRequestMapping.class);
				String url = CommenUtil.ifContainsSprit(anno.value());
				for (Method method : clazz.getMethods()) {
					if (method.isAnnotationPresent(MyRequestMapping.class)) {
						MyRequestMapping reqAnno = method.getAnnotation(MyRequestMapping.class);
						url += CommenUtil.ifContainsSprit(reqAnno.value());
					} else {
						break;
					}
					System.out.println(url);
					handlerMapping.put(url, method);
				}
			}

		}
	}

}
