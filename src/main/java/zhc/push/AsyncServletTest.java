package zhc.push;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AsyncServletTest {
	private ExecutorService executorService = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	
	static class MyListener implements ServletContextListener {

		@Override
		public void contextInitialized(ServletContextEvent sce) {
			// TODO 初始化线程池
//			ServletContextListener.super.contextInitialized(sce);
		}

		@Override
		public void contextDestroyed(ServletContextEvent sce) {
			// TODO 关闭线程池
//			ServletContextListener.super.contextDestroyed(sce);
		}
	}
	//2.
	class MyWork implements Runnable {

		AsyncContext asyncContext;
		public MyWork(AsyncContext asyncContext) {
			this.asyncContext = asyncContext;
		}
		
		@Override
		public void run() {
			// TODO 做实际的业务工作，将结果通过asyncContext返回给客户端
			
		}
	}
	//3.实现监听器
	class MyAsyncListenerImpl implements AsyncListener {

		@Override
		public void onComplete(AsyncEvent event) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTimeout(AsyncEvent event) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(AsyncEvent event) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStartAsync(AsyncEvent event) throws IOException {
			// TODO Auto-generated method stub
			
		}
		
	}

	//4.
	@WebServlet(asyncSupported=true)
	class MyAsyncBusiServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			super.doGet(req, resp);
			//启动异步
			AsyncContext asyncContext = req.startAsync();
			asyncContext.addListener(new MyAsyncListenerImpl());
			executorService.execute(new MyWork(asyncContext));
		}
	}
}
