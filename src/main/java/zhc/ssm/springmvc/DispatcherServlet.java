package zhc.ssm.springmvc;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

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
		System.out.println("scanPackage start");
		URL url = getClass().getClassLoader().getResource("/"+packageName.replace(".", "/"));
		String path = url.getFile();
		File file = new File(path);
		String[] fileNames = file.list();
		for (String fileName : fileNames) {
			File f = new File(path+fileName);
			System.out.println(path+"||"+f.getName()+"||"+fileName+"||"+file.getName());
			if (f.isDirectory()) {
				scanPackage(path+f.getName());
			} else {
				files.add(path+f.getName());
			}
		}
		System.out.println("scanPackage end");
	}
}
