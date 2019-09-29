package zhc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.ReferenceCountUtil;

/**
 * ClassName: zhc.netty.NettyEchoServer 
 * @Description: TODO
 * @author zhc
 * @date 2019年9月27日
 */
public class NettyEchoServer {
	public static void main(String[] args) {
		new NettyEchoServer().run();
	}
	
	private void run() {
		// 线程池是提升服务器性能的重要技术手段，利用定长的线程池可以保证核心线程的有效数量
        // 在Netty之中线程池的实现分为两类：主线程池（接收客户端连接）、工作线程池（处理客户端连接）
        EventLoopGroup bossGroup = new NioEventLoopGroup(10); // 创建接收线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup(20); // 创建工作线程池
        System.out.println("服务器启动成功，监听端口为：" + HostInfo.PORT);
        try {
            // 创建一个服务器端的程序类进行NIO启动，同时可以设置Channel
            ServerBootstrap serverBootstrap = new ServerBootstrap();   // 服务器端
            // 设置要使用的线程池以及当前的Channel类型
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            // 接收到信息之后需要进行处理，于是定义子处理器
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536,0,4,0,4));	//拆包器
                    socketChannel.pipeline().addLast(new JsonDecoder()) ;
                    socketChannel.pipeline().addLast(new LengthFieldPrepender(4)) ;
                    socketChannel.pipeline().addLast(new JsonEncoder()) ;
                    socketChannel.pipeline().addLast(new EchoServerHandler()); // 追加了处理器
                }
            });
            // 可以直接利用常亮进行TCP协议的相关配置
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // ChannelFuture描述的时异步回调的处理操作
            ChannelFuture future = serverBootstrap.bind(HostInfo.PORT).sync();
            future.channel().closeFuture().sync();// 等待Socket被关闭
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully() ;
            bossGroup.shutdownGracefully() ;
        }
	}
	
	static class EchoServerHandler extends ChannelInboundHandlerAdapter {
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	        try {
	            System.out.println(msg.getClass() + " **************");
	            Member member = (Member) msg ;
	            System.err.println("{服务器}" + member);
	            member.setName("【ECHO】" + member.getName());
	            ctx.writeAndFlush(member); // 回应的输出操作
	        } finally {
	            ReferenceCountUtil.release(msg) ; // 释放缓存
	        }
	    }
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        cause.printStackTrace();
	        ctx.close() ;
	    }
	}
}
