package zhc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.ReferenceCountUtil;

public class NettyEchoClient {
	
	public static void main(String[] args) {
		new NettyEchoClient().run();
	}
	
	public void run() {
		// 1、如果现在客户端不同，那么也可以不使用多线程模式来处理;
        // 在Netty中考虑到代码的统一性，也允许你在客户端设置线程池
		EventLoopGroup group = new NioEventLoopGroup();	//创建一个线程池
		try {
			Bootstrap client = new Bootstrap();			//客户端处理程序
			client.group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4));//拆包器
                    socketChannel.pipeline().addLast(new JSONDecoder());
                    socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                    socketChannel.pipeline().addLast(new JSONEncoder());
                    socketChannel.pipeline().addLast(new EchoClientHandler()); // 追加了处理器
				}
			});
			ChannelFuture channelFuture = client.connect(HostInfo.HOST_NAME, HostInfo.PORT).sync();
            channelFuture.channel().closeFuture().sync(); // 关闭连接
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
	
	static  class EchoClientHandler extends ChannelInboundHandlerAdapter {
//		private static final int REPEAT = 500;// 消息重复发送次数

	    @Override
	    public void channelActive(ChannelHandlerContext ctx) throws Exception {
	        Member member = new Member();   // 现在直接进行对象的发送
	        member.setMid("xiaoli");
	        member.setName("小李老师");
	        member.setAge(16);
	        member.setSalary(1.1);
	        ctx.writeAndFlush(member) ; // 直接进行对象实例发送
	    }

	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	        // 只要服务器端发送完成信息之后，都会执行此方法进行内容的输出操作
	        try {
	            Member member = (Member) msg ;
	            System.out.println(member); // 输出服务器端的响应内容
	        } finally {
	            ReferenceCountUtil.release(msg); // 释放缓存
	        }
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        cause.printStackTrace();
	        ctx.close();
	    }
	}
}
