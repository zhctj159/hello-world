package zhc.ssm.springmvc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DispatcherServlet")
public class DispatcherServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    List<String> packageNames=new ArrayList<String>(); 
    //所有类的实例 key是注解的value, value是所有类的实例
    Map<String,Object> instanceMap=new HashMap<String,Object>(); 
    Map<String,Object> handerMap=new HashMap<String,Object>();
    
    /** @see HttpServlet#HttpServlet() */
    public DispatcherServlet2() {
        super();
    }

    /** @see Servlet#init(ServletConfig) */
    public void init(ServletConfig config) throws ServletException {
        //包扫描，获取包中的文件
        scanPackage("zhc.ssm.springmvc");
        try {
            filterAndInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //建立一个映射关系
        handerMap();
        
        ioc();//实现注入
    }
    
    private void scanPackage(String basePackage){
        URL url=this.getClass().getClassLoader().getResource("/"+basePackage.replaceAll("\\.","/"));//将所有.转义获取对应的路径
        String pathfile=url.getFile();
        File file=new  File(pathfile);
        String[] files=file.list();
        for (String path : files) {
            File eachFile= new File(pathfile+path);//有点问题
            if(eachFile.isDirectory()){
                scanPackage(basePackage+"."+eachFile.getName());
            }else{
                packageNames.add(basePackage+"."+eachFile.getName());
            }
        }
    }

    public void handerMap(){
        if(instanceMap.size()<=0)
            return;
        for(Map.Entry<String, Object> entry:instanceMap.entrySet()){
        	Class<?> clazz = entry.getValue().getClass();
            if(clazz.isAnnotationPresent(MyController.class)){
                MyController controller = clazz.getAnnotation(MyController.class);
                String ctvalue= controller.value();
                Method[] methods= clazz.getMethods();
                for(Method method:methods){
                    if(method.isAnnotationPresent(MyRequestMapping.class)){
                        MyRequestMapping rm= method.getAnnotation(MyRequestMapping.class);
                        String rmvalue = rm.value();
                        handerMap.put("/"+ctvalue+"/"+rmvalue,method);
                    }else{
                        continue;
                     }
                 }
             }else{
                 continue;
             }   
             
         }
     }
     public void ioc(){
         if(instanceMap.isEmpty())return;
         for(Map.Entry<String, Object> entry:instanceMap.entrySet()){
             Field[] fields=    entry.getValue().getClass().getDeclaredFields();//拿到类里面的属性
             for (Field field : fields) {
                 field.setAccessible(true);
                 if(field.isAnnotationPresent(MyAutowired.class)){
                	 MyAutowired qf = field.getAnnotation(MyAutowired.class);
                     String value= qf.value();
                     
                     field.setAccessible(true);
                     try {
                         field.set(entry.getValue(), instanceMap.get(value));
                     } catch (IllegalArgumentException e) {
                         e.printStackTrace();
                     } catch (IllegalAccessException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
         
     }
     public void filterAndInstance() throws Exception{
         if(packageNames.size()<=0){
             return;
         }
         for (String classname : packageNames) {
             Class<?> clazz =Class.forName(classname.replace(".class",""));
             if(clazz.isAnnotationPresent(MyController.class)){
                 Object instance = clazz.newInstance();
                 MyController an= clazz.getAnnotation(MyController.class);
                 String key=an.value();
                 instanceMap.put(key,instance);
             }else if(clazz.isAnnotationPresent(MyService.class)){
                 Object instance=     clazz.newInstance();
                 MyService an= clazz.getAnnotation(MyService.class);
                 String key=an.value();
                 instanceMap.put(key,instance);
             }else{
                 continue;
             }
         }
     }
     
     /** @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) */
     protected void doGet(HttpServletRequest request,
             HttpServletResponse response) throws ServletException, IOException {
         this.doPost(request, response);
     }
 
     /** @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) */
     protected void doPost(HttpServletRequest request,
             HttpServletResponse response) throws ServletException, IOException {
         String url= request.getRequestURI();
         String context=request.getContextPath();
         String path=url.replace(context,"");
         Method method    =(Method) handerMap.get(path);
         SpringmvcController controller=(SpringmvcController) instanceMap.get(path.split("/")[1]); 
         try {
             method.invoke(controller, new Object[]{request,response,null});
         } catch (IllegalAccessException e) {
             e.printStackTrace();
         } catch (IllegalArgumentException e) {
             e.printStackTrace();
         } catch (InvocationTargetException e) {
             e.printStackTrace();
         }
     }
}
