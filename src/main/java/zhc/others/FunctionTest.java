package zhc.others;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FunctionTest {
	/**
	 * name	type	description
		Consumer	Consumer<T>	接收T对象，不返回值
		Predicate	Predicate<T>	接收T对象并返回boolean
		Function	Function<T, R>	接收T对象，返回R对象
		Supplier	Supplier<T>	提供T对象（例如工厂），不接收值
		UnaryOperator	UnaryOperator	接收T对象，返回T对象
		BinaryOperator	BinaryOperator	接收两个T对象，返回T对象
	 * @param args
	 */
	public static void main(String[] args) {
		Function<Integer, Integer> dou = e -> e*2;
		Function<Integer, Integer> square = e -> e*e;
		int value = dou.andThen(square).apply(3);
		System.out.println("java.util.function.Function "+value);
		int value2 = dou.compose(square).apply(3);
        System.out.println("java.util.function.Function " + value2);
        //返回一个执行了apply()方法之后只会返回输入参数的函数对象
        Object identity = Function.identity().apply("huohuo");
        System.out.println("java.util.function.Function.identity() "+identity);
        
        BiFunction<String, String, String> function = (t,u)->t+""+u;
        System.out.println("java.util.function.BiFunction "+function.apply("Hello", "World"));
        
        MyBiFunction myBiFunction = new MyBiFunction();
        String s = myBiFunction.apply("Hello", "World");
        System.out.println("MyBiFunction "+s);

        MyFunction myFunction = new MyFunction();
        Integer len = myFunction.apply(s);
        System.out.println("MyFunction "+len);
        
        Consumer<String> c1 = t -> {
        	System.out.println("java.util.function.Consumer start1");
        	System.out.println(t);
        	System.out.println("java.util.function.Consumer end1");
        };
        Consumer<String> c2 = t -> {
        	System.out.println("java.util.function.Consumer start2");
        	System.out.println(t);
        	System.out.println("java.util.function.Consumer end2");
        };
        c1.andThen(c2).accept("Hello,World");
        
        Predicate<String> p1 = t -> t==null?true:false;
        Predicate<String> p2 = t -> t.length()>20?true:false;
        boolean b = p1.negate().and(p2).test("Hello,World!");
        System.out.println("java.util.function.Predicate "+b);
        
        Supplier<Integer> s1 = () -> new Integer(16);
        System.out.println("java.util.function.Supplier "+s1.get().intValue());
        
        UnaryOperator<String> u1 = t->t+",hello";
        System.out.println("java.util.function.UnaryOperator "+u1.apply("zhaihc3"));
        
        BinaryOperator<String> bo1 = (n1,n2) -> n1+","+n2;
        System.out.println(bo1.apply("hello", "world"));
        BinaryOperator<Integer> bo2 = BinaryOperator.minBy(Comparator.naturalOrder());
        System.out.println(bo2.apply(3, 2));
	}
	
	/**
	 * 字符串长度记录返回
	 */
	static class MyFunction implements Function<String, Integer> {
		@Override
		public Integer apply(String s) {
			return s.length();
		}
	}
	
	/**
	 * 两个字符串的连接
	 */
	static class MyBiFunction implements BiFunction<String, String, String> {
		@Override
		public String apply(String t, String u) {
			return t+""+u;
		}
	}
}
