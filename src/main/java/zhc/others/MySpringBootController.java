package zhc.others;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

//@CrossOrigin("http://127.0.0.1:8080")
//@Controller("zhc")
@RestController
public class MySpringBootController {

	@RequestMapping("/myMethod")
	public void myMethod() {
		System.out.println("MyMethod!");
	}

	@Autowired
	private DeferredResultService deferredResultService;

	/** 为了方便测试，简单模拟一个 多个请求用同一个requestId会出问题 */
	private final String requestId = "haha";

	@RequestMapping(value="/deferredResult/get",method=RequestMethod.GET)
	public DeferredResult<DeferredResultResponse> get(
			@RequestParam(value = "timeout", required = false, defaultValue = "10000") Long timeout) {
		DeferredResult<DeferredResultResponse> deferredResult = new DeferredResult<>(timeout);
		deferredResultService.process(requestId, deferredResult);
		return deferredResult;
	}

	/** 设置DeferredResult对象的result属性，模拟异步操作 */
	@RequestMapping(value="/deferredResult/result",method=RequestMethod.GET)
	public String settingResult(
			@RequestParam(value = "desired", required = false, defaultValue = "成功") String desired) {
		DeferredResultResponse deferredResultResponse = new DeferredResultResponse();
		if ("成功".equals(desired)) {
			deferredResultResponse.setCode(HttpStatus.OK.value());
			deferredResultResponse.setMsg(desired);
		} else {
			deferredResultResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			deferredResultResponse.setMsg("失败");
		}
		deferredResultService.settingResult(requestId, deferredResultResponse);

		return "Done";
	}
}

@Service
class DeferredResultService {
	private Map<String, Consumer<DeferredResultResponse>> taskMap;

	public DeferredResultService() {
		taskMap = new ConcurrentHashMap<>();
	}

	/** 将请求id与setResult映射 */
	public void process(String requestId, DeferredResult<DeferredResultResponse> deferredResult) {
		// 请求超时的回调函数
		deferredResult.onTimeout(() -> {
			taskMap.remove(requestId);
			DeferredResultResponse deferredResultResponse = new DeferredResultResponse();
			deferredResultResponse.setCode(HttpStatus.REQUEST_TIMEOUT.value());
			deferredResultResponse.setMsg("超时");
			deferredResult.setResult(deferredResultResponse);
		});

		Optional.ofNullable(taskMap).filter(t -> !t.containsKey(requestId))
				.orElseThrow(() -> new IllegalArgumentException(String.format("requestId=%s is existing", requestId)));

		// 这里的Consumer-deferredResult::setResult相当于是传入的DeferredResult对象的地址
		taskMap.putIfAbsent(requestId, deferredResult::setResult);
	}

	/** 这里相当于异步的操作方法 设置DeferredResult对象的setResult方法 */
	public void settingResult(String requestId, DeferredResultResponse deferredResultResponse) {
		if (taskMap.containsKey(requestId)) {
			//根据requestId拿到方法deferredResult::setResult的入口
			Consumer<DeferredResultResponse> deferredResultResponseConsumer = taskMap.get(requestId);
			// 这里相当于DeferredResult对象的setResult方法，入参为deferredResultResponse
			deferredResultResponseConsumer.accept(deferredResultResponse);
			taskMap.remove(requestId);
		}
	}
}

class DeferredResultResponse {
	private Integer code;
    private String msg;

    public Integer getCode() {
		return code;
	}
    public void setCode(Integer code) {
		this.code = code;
	}
    public String getMsg() {
		return msg;
	}
    public void setMsg(String msg) {
		this.msg = msg;
	}
}
