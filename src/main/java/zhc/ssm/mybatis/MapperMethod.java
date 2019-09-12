package zhc.ssm.mybatis;

/**
 * 对应一条语句，一个接口方法
 */
public class MapperMethod {
	private String sqlType;
	private String methodName;
	private String sqlText;
	private Object resultType;
	private String parameterType;
	public String getSqlType() {
		return sqlType;
	}
	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getSqlText() {
		return sqlText;
	}
	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}
	public Object getResultType() {
		return resultType;
	}
	public void setResultType(Object resultType) {
		this.resultType = resultType;
	}
	public String getParameterType() {
		return parameterType;
	}
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
}
