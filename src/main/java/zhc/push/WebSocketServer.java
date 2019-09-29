package zhc.push;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@ServerEndpoint(value="/ws/asset")
@Component
public class WebSocketServer {
	private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
	/** 线程安全Set，用来存放每个客户端对应的session对象 */
	private static CopyOnWriteArraySet<Session> sessionSet = new CopyOnWriteArraySet<Session>();
	/** 线程安全Map，用来存放每个客户端sessionid和用户名的对应关系 */
	private static Map<String, String> sessionMap = new ConcurrentHashMap<String, String>();
	
	/** 连接建立成功调用的方法 */
	@OnOpen
	public void onOpen(Session session) {
		//将用户session、session和用户名对应关系放入本地缓存
		sessionSet.add(session);
		Map<String, List<String>> pathParameters = session.getRequestParameterMap();
		String userId = pathParameters.get("toUserId").get(0);
		sessionMap.put(session.getId(), userId);
		System.out.println("有连接加入，当前连接数为："+ONLINE_COUNT.incrementAndGet());
		try {
			broadCastInfo("系统消息@^用户["+userId+"]加入群聊");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 连接关闭调用的方法 */
	@OnClose
	public void onClose(Session session) {
		//将用户session，sessionid和用户名的对应关系从本地缓存移除
		sessionSet.remove(session);
		String userId = sessionMap.get(session.getId());
		sessionMap.remove(session.getId());
		int cnt = ONLINE_COUNT.decrementAndGet();
		System.out.println("有连接关闭，当前连接数为："+cnt);
		try {
			broadCastInfo("系统消息@^用户["+userId+"]退出群聊");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 收到客户端消息调用的方法 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("来自客户端"+sessionMap.get(session.getId())+"的消息："+message);
		if (message.startsWith("ToUser:")) {
			// TODO 这里可以实现一对一聊天sendMessageAlone()
		} else {
			//实现群聊
			String msger = sessionMap.get(session.getId());
			try {
				broadCastInfo(msger+"@^"+message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void broadCastInfo(String string) {
		// TODO Auto-generated method stub
		
	}

	/** 错误处理的方法 */
	@OnError
	public void onError(Session session, Throwable error) {
		System.err.println("发生错误："+error.getMessage()+"，Session ID："+session.getId());
		error.printStackTrace();
	}
	
	/** 发送消息的基础方法 */
	public static void basicSendMessage(Session session, String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			System.err.println("发送消息出错："+e.getMessage());
			e.printStackTrace();
		}
	}
}
