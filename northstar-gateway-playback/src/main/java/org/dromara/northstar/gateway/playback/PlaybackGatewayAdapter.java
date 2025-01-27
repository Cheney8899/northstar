package org.dromara.northstar.gateway.playback;

import org.dromara.northstar.common.constant.ChannelType;
import org.dromara.northstar.common.constant.ConnectionState;
import org.dromara.northstar.common.model.GatewayDescription;
import org.dromara.northstar.gateway.MarketGateway;

import xyz.redtorch.pb.CoreField.ContractField;

public class PlaybackGatewayAdapter implements MarketGateway {
	
	private PlaybackContext ctx;
	
	private GatewayDescription gd;
	
	private ConnectionState connState = ConnectionState.DISCONNECTED;
	
	public PlaybackGatewayAdapter(PlaybackContext ctx, GatewayDescription gd) {
		this.ctx = ctx;
		this.gd = gd;
		ctx.setOnStopCallback(() -> connState = ConnectionState.DISCONNECTED);
	}

	@Override
	public void connect() {
		connState = ConnectionState.CONNECTED;
		ctx.start();
	}

	@Override
	public void disconnect() {
		ctx.stop();
		connState = ConnectionState.DISCONNECTED;
	}
	
	@Override
	public ConnectionState getConnectionState() {
		return connState;
	}

	@Override
	public boolean getAuthErrorFlag() {
		return false;
	}

	@Override
	public boolean subscribe(ContractField contract) {
		// 动态订阅不需要实现
		return true;
	}

	@Override
	public boolean unsubscribe(ContractField contract) {
		// 动态取消订阅不需要实现
		return true;
	}

	@Override
	public boolean isActive() {
		return ctx.isRunning();
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.PLAYBACK;
	}

	@Override
	public GatewayDescription gatewayDescription() {
		gd.setConnectionState(getConnectionState());
		return gd;
	}

	@Override
	public String gatewayId() {
		return gd.getGatewayId();
	}

}
