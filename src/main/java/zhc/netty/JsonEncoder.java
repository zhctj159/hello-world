package zhc.netty;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * ClassName: zhc.netty.JsonEncoder 
 * @Description: TODO
 * @author zhc
 * @date 2019年9月27日
 */
public class JsonEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        byte data [] = JSONObject.toJSONString(msg).getBytes() ;
        out.writeBytes(data) ;
    }
}
