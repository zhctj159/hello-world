package zhc.others;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class HystrixFallbackTest extends HystrixCommand<String> {
	
	public static void main(String[] args) {
		System.out.println(new HystrixFallbackTest("Morning", new MorningService()).execute());
	}
	
	private final String name;
	private MorningService morningService;

	public HystrixFallbackTest(String name, MorningService morningService) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("FallbackGroup")).andCommandPropertiesDefaults(
				HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(10000))); // 10秒超时哦
		this.name = name;
		this.morningService = morningService;
	}

	@Override
	protected String run() throws Exception {
		morningService.timeout();
		return name;
	}

	@Override
	protected String getFallback() {
		return "fallback: " + name;
	}
	
	private static class MorningService {
		/** 超时 */
		public void timeout() {
			int j = 0;
//			System.out.println("j = "+j);
//			while (true) {
//				j++;
//			}
			while (j<100000000) {
				j++;
			}
		}
	}
}
