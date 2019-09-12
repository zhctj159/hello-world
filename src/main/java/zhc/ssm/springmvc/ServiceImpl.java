package zhc.ssm.springmvc;

@MyService("service")
public class ServiceImpl implements Service{

	@Override
	public String select() {
		System.out.println("ServiceImpl select");
		return "ServiceImpl select";
	}

	@Override
	public boolean insert(String value) {
		System.out.println("ServiceImpl insert, value: "+value);
		return false;
	}

}
