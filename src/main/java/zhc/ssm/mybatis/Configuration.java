package zhc.ssm.mybatis;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/** 读取数据库配置及mapper配置文件 */
public class Configuration {
	private static ClassLoader loader;
	
	private static Configuration instance = new Configuration();
	
	private Configuration() {
		loader = ClassLoader.getSystemClassLoader();
		build("dbInfo.xml");
		for (File file : new File("./target/classes/").listFiles()) {
			if (file.isFile()) {
				String fileName = file.getName();
				if (fileName.endsWith(".xml")) {
					loadMapper(fileName);
				}
			}
		}
//		loadMapper("UserMapper.xml");
//		loadMapper("User2Mapper.xml");
	}
	
	public static Configuration getInstance() {
		return instance;
	}
	
	private String driverClassName = null;
	private String url = null;
	private String username = null;
	private String password = null;
	
	public void build(String resource) {
		try {
			InputStream is = loader.getResourceAsStream(resource);
			SAXReader reader = new SAXReader();
			Document document = reader.read(is);
			Element root = document.getRootElement();
			evalDataSource(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void evalDataSource(Element node) {
		if (!node.getName().equals("database")) {
			throw new RuntimeException("root should be <database>");
		}
		for (Object item : node.elements()) {
			Element i = (Element)item;
			String value = i.hasContent()?i.getText():i.attributeValue("value");
			String name = i.attributeValue("name");
			if (null==name || null==value) {
				throw new RuntimeException("[database]: <property> should contain name and value");
			}
			switch (name) {
			case "url":
				url = value;
				break;
			case "username":
				username = value;
				break;
			case "password":
				password = value;
				break;
			case "driverClassName":
				driverClassName = value;
				break;
			default:
				throw new RuntimeException("[database]: <property> unknown name");
			}
		}
	}
	
	public Connection getConnection() {
		try {
			Class.forName(driverClassName);
			Connection conn = DriverManager.getConnection(url, username, password);
			return conn;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Map<String, List<MapperMethod>> interfaces = new HashMap<String, List<MapperMethod>>();

	public Map<String, List<MapperMethod>> getInterfaces() {
		return interfaces;
	}
	
	public List<MapperMethod> getMethods(String interfaceName) {
		if (interfaces.containsKey(interfaceName)) {
			return interfaces.get(interfaceName);
		}
		throw new RuntimeException("the interface "+interfaceName+" not exists!");
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> clazz){
		String interfaceName = clazz.getName();
		if (interfaces.containsKey(interfaceName)) {
			return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new MyMapperProxy());
		}
		throw new RuntimeException("the interface "+interfaceName+" not exists!");
	}
	
	class MyMapperProxy implements InvocationHandler {
		private Executor executor = new Executor();
		
		/** 从配置文件中找到要代理的接口方法对应的sql语句等参数并使用执行器执行 */
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String interfaceName = method.getDeclaringClass().getName();
			List<MapperMethod> methods = getMethods(interfaceName);
			if (null!=methods && 0!=methods.size()) {
				for (MapperMethod m : methods) {
					if (method.getName().equals(m.getMethodName())) {
						return executor.query(m.getSqlText(), String.valueOf(args[0]));
					}
				}
			}
			return null;
		}
	}
	
	public void loadMapper(String path) {
		try {
			InputStream is = loader.getResourceAsStream(path);
			SAXReader reader = new SAXReader();
			Document document = reader.read(is);
			Element root = document.getRootElement();
			if (!root.getName().equals("mapper")) {
				return;
			}
			String interfaceName = root.attributeValue("namespace");
			List<MapperMethod> methods = new ArrayList<MapperMethod>();
			for (Iterator<?> rootIter = root.elementIterator(); rootIter.hasNext();) {
				MapperMethod method = new MapperMethod();
				Element e = (Element)rootIter.next();
				String sqlType = e.getName().trim();
				String methodName = e.attributeValue("id").trim();
				String sqlText = e.getText().trim();
				String resultType = e.attributeValue("resultType").trim();
				method.setSqlType(sqlType);
				method.setMethodName(methodName);
				Object newInstance = null;
				try {
					newInstance = Class.forName(resultType).newInstance();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				method.setResultType(newInstance);
				method.setSqlText(sqlText);
				methods.add(method);
			}
			System.out.println("loadMapper: "+interfaceName);
			interfaces.put(interfaceName, methods);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
