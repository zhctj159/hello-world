package zhc.ssm.springmvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static List<String> files = new ArrayList<String>();
	private static Map<String, Object> instances = new HashMap<String, Object>();
	private static Map<String, Method> handlerMap = new HashMap<String, Method>();
	
	public DispatcherServlet() {
		super();
		System.out.println("DispatcherServlet end");
	}

	@Override
	public void init() throws ServletException {
		System.out.println("init start");
		//扫描包文件
		scanPackage("zhc.ssm.springmvc");
		//将其中的class文件加载成实例(单例)
		filterAndInstance();
		//构建url与method之间的映射
		handlerMap();
		//自动装配，注入字段
		ioc();
		System.out.println("init end");
	}

	private void ioc() {
		System.out.println("ioc start");
		if (instances.isEmpty()) {
			return;
		}
		for (Entry<String, Object> entry : instances.entrySet()) {
			Class<?> clazz = entry.getValue().getClass();
			if (clazz.isAnnotationPresent(MyController.class)) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(MyAutowired.class)) {
						MyAutowired qualifier = field.getAnnotation(MyAutowired.class);
						field.setAccessible(true);
						try {
							field.set(entry.getValue(), instances.get(qualifier.value()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		System.out.println("ioc end");
	}

	private void handlerMap() {
		System.out.println("handlerMap start");
		if (instances.isEmpty()) {
			return;
		}
		for (Entry<String, Object> entry : instances.entrySet()) {
			Class<?> clazz = entry.getValue().getClass();
			if (clazz.isAnnotationPresent(MyController.class)) {
				MyController controller = clazz.getAnnotation(MyController.class);
				String t1 = controller.value();
				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(MyRequestMapping.class)) {
						MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
						String t2 = requestMapping.value();
						handlerMap.put(t1+"/"+t2, method);
					}
				}
			}
		}
		System.out.println("handlerMap end");
	}

	private void filterAndInstance() {
		System.out.println("filterAndInstance start");
		if (files.isEmpty()) {
			return;
		}
		for (String file : files) {
			try {
				Class<?> clazz = Class.forName(file);
				if (clazz.isAnnotationPresent(MyController.class)) {
					MyController controller = clazz.getAnnotation(MyController.class);
					instances.put(controller.value(), clazz.newInstance());
				} else if (clazz.isAnnotationPresent(MyService.class)) {
					MyService service = clazz.getAnnotation(MyService.class);
					instances.put(service.value(), clazz.newInstance());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("filterAndInstance end");
	}

	private void scanPackage(String packageName) {
		System.out.println("scanPackage start: " + packageName);
		files.add("zhc.ssm.springmvc.DispatcherServlet");
		files.add("zhc.ssm.springmvc.MyAutowired");
		files.add("zhc.ssm.springmvc.MyController");
		files.add("zhc.ssm.springmvc.MyRequestMapping");
		files.add("zhc.ssm.springmvc.MyService");
		files.add("zhc.ssm.springmvc.Service");
		files.add("zhc.ssm.springmvc.ServiceImpl");
		files.add("zhc.ssm.springmvc.SpringmvcController");
//		ClassLoader loader = getClass().getClassLoader();
//		URL url = loader.getResource("/"+packageName.replace(".", "/"));
//		String path = url.getFile();
//		File file = new File(path);
//		String[] fileNames = file.list();
//		for (String fileName : fileNames) {
//			File f = new File(path+fileName);
//			System.out.println(path+"||"+f.getName()+"||"+fileName+"||"+file.getName());
//			if (f.isDirectory()) {
//				scanPackage(path+f.getName());
//			} else {
//				files.add(path+f.getName());
//			}
//		}
		System.out.println("scanPackage end");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}
 
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURI();
		String context = req.getContextPath();
		String path = url.replace(context, "");
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		Method method = handlerMap.get(path);
		SpringmvcController controller = (SpringmvcController) instances.get(path.split("/")[0]);
		try {
			Object ret = method.invoke(controller, new Object[] { req, resp });
			PrintWriter out = resp.getWriter();
			out.println(ret);
			out.close();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
