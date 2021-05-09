package tech.xuanwu.northstar.handler;

import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.event.NorthstarEvent;
import tech.xuanwu.northstar.common.event.NorthstarEventType;
import tech.xuanwu.northstar.common.exception.NoSuchElementException;
import tech.xuanwu.northstar.domain.GatewayConnection;
import tech.xuanwu.northstar.model.GatewayAndConnectionManager;

@Slf4j
public class ConnectionEventHandler extends AbstractEventHandler implements InternalEventHandler{
	
	protected GatewayAndConnectionManager gatewayConnMgr;
	
	private final Set<NorthstarEventType> TARGET_TYPE = new HashSet<>() {
		private static final long serialVersionUID = 6418831877479036414L;
		{
			this.add(NorthstarEventType.CONNECTING);
			this.add(NorthstarEventType.CONNECTED);
			this.add(NorthstarEventType.DISCONNECTED);
			this.add(NorthstarEventType.DISCONNECTING);
		}
	};
	
	public ConnectionEventHandler(GatewayAndConnectionManager gatewayConnMgr) {
		this.gatewayConnMgr = gatewayConnMgr;
	}

	@Override
	public void doHandle(NorthstarEvent e) {
		String gatewayId = (String) e.getData();
		if(!gatewayConnMgr.exist(gatewayId)) {
			throw new NoSuchElementException("没有找到相关的网关：" + gatewayId);
		}
		GatewayConnection conn = gatewayConnMgr.getGatewayConnectionById(gatewayId);
		if(e.getEvent() == NorthstarEventType.CONNECTING) {
			log.info("[{}]-连接中", gatewayId);
			conn.onConnecting();
		} else if(e.getEvent() == NorthstarEventType.DISCONNECTING) {
			log.info("[{}]-断开中", gatewayId);
			conn.onDisconnecting();
		} else if(e.getEvent() == NorthstarEventType.CONNECTED) {
			log.info("[{}]-已连接", gatewayId);
			conn.onConnected();
		} else if(e.getEvent() == NorthstarEventType.DISCONNECTED) {
			log.info("[{}]-已断开", gatewayId);
			conn.onDisconnected();
		}
		
	}

	@Override
	public boolean canHandle(NorthstarEventType eventType) {
		return TARGET_TYPE.contains(eventType);
	}

}