package zhc.others;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 模拟使用DelayQueue的场景：订单下达之后，未支付超时取消。
 * @author zhc
 * @time 2019年8月13日 下午4:39:21
 */
public class DelayQueueTest {
    /** 是否开启自动取消功能 */
    static int isStarted = 1;
    /** 延迟队列，用来存放订单对象 */
    static DelayQueue<Order> queue = new DelayQueue<>();

    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //新建一个线程，用来模拟定时取消订单job
        ExecutorService executorService = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>());
		executorService.execute(() -> {
            System.out.println("开启自动取消订单job,当前时间："+ LocalDateTime.now().format(formatter));
            while (isStarted == 1) {
                try {
                    Order order = queue.take();
                    order.setStatus("CANCELED");

                    System.out.println("订单：" + order.getOrderNo() + " 付款超时，自动取消，当前时间："+ LocalDateTime.now().format(formatter));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });
		
		//新建一个线程，模拟提交订单
		executorService.execute(() -> {
            //定义最早的订单的创建时间
            long beginTime = System.currentTimeMillis();
            //下面模拟3个订单，每个订单的创建时间依次延后3秒
            queue.add(new Order("SO001", 100, "CREATED", new Date(beginTime), new Date(beginTime + 3000)));
            beginTime += 3000L;
            queue.add(new Order("SO002", 100, "CREATED", new Date(beginTime), new Date(beginTime + 3000)));
            beginTime += 3000L;
            queue.add(new Order("SO003", 100, "CREATED", new Date(beginTime), new Date(beginTime + 3000)));
        });
		executorService.shutdown();
    }
}

/**
 * 订单类，用于存放订单头信息
 */
class Order implements Delayed {
    String orderNo;		//订单号
    int cost;			//费用
    String status;		//订单状态
    Date createTime;	//创建时间
    Date cancelTime;	//取消时间

    public Order(String orderNo, int cost, String status, Date createTime, Date cancelTime) {
        this.orderNo = orderNo;
        this.cost = cost;
        this.status = status;
        this.createTime = createTime;
        this.cancelTime = cancelTime;
    }

    public String getOrderNo() {
        return orderNo;
    }
    public int getCost() {
        return cost;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public Date getCancelTime() {
        return cancelTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        //下面用到unit.convert()方法，其实在这个小场景不需要用到，只是学习一下如何使用罢了
        long l = unit.convert(cancelTime.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        return l;
    }

    @Override
    public int compareTo(Delayed o) {
        //这里根据取消时间来比较，如果取消时间小的，就会优先被队列提取出来
        return this.getCancelTime().compareTo(((Order) o).getCancelTime());
    }
}
