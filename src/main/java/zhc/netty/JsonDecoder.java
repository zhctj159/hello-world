package zhc.netty;

import java.util.List;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * ClassName: zhc.netty.JsonDecoder 
 * @Description: TODO
 * @author zhc
 * @date 2019年9月27日
 */
public class JsonDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> list) throws Exception {
        int len = msg.readableBytes(); // 可以用的数据长度
        byte data[] = new byte[len];
        msg.getBytes(msg.readerIndex(), data, 0, len);
        list.add(JSON.parseObject(new String(data)).toJavaObject(Member.class)) ;
    }
}
