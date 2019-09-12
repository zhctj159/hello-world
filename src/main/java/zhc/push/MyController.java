package zhc.push;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

@Controller
@RequestMapping(produces="text/html;charset=UTF-8")
@CrossOrigin(origins="*",maxAge=3600)
public class MyController {
	private static String[] NEWS = {
			"new1",
			"new2",
			"andfaodfjawoefw",
			"你好啊发发打发违法",
			"青青草权出去入场从3查3促30从",
			"23013r1c12cqwcq未来可出去吃"
	};
	private static ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	@RequestMapping(value="/realTimeNews")
	@ResponseBody
	public DeferredResult<String> realTimeNews() {
		final DeferredResult<String> deferredResult = new DeferredResult<String>();
//		executorService.submit((Runnable)()->{
//			try {
//				Thread.sleep(2000);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			int index = new Random().nextInt(NEWS.length);
//			// spring发现执行deferredResult.setResult后，会自动调用getResult返回给浏览器
//			deferredResult.setResult(NEWS[index]);
//			System.out.println(index);
//		});
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					e.printStackTrace();
				}
				int index = new Random().nextInt(NEWS.length);
				// spring发现执行deferredResult.setResult后，会自动调用getResult返回给浏览器
				deferredResult.setResult(NEWS[index]);
				System.out.println(index);			
			}
		});
		return deferredResult;
	}
	
	/** 显示服务器时间 */
	@RequestMapping(value="showTime",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	@RequestMapping("/time")
	@ResponseBody
	public String normal() {
		return "showtime";
	}
	
}
