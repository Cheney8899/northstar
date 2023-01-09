package tech.quantit.northstar.gateway.binance;

import tech.quantit.northstar.common.constant.ChannelType;
import tech.quantit.northstar.common.event.FastEventEngine;
import tech.quantit.northstar.common.model.GatewayDescription;
import tech.quantit.northstar.gateway.api.MarketGateway;
import xyz.redtorch.pb.CoreEnum.GatewayTypeEnum;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

public class BinanceMarketGatewayAdapter implements MarketGateway {

	private FastEventEngine feEngine;
	
	private GatewayDescription gd;
	
	public BinanceMarketGatewayAdapter(GatewayDescription gd, FastEventEngine feEngine) {
		this.gd = gd;
		this.feEngine = feEngine;
	}
	
	@Override
	public GatewaySettingField getGatewaySetting() {
		return GatewaySettingField.newBuilder()
				.setGatewayId(gd.getGatewayId())
				.setGatewayType(GatewayTypeEnum.GTE_MarketData)
				.build();
	}

	@Override
	public void connect() {
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getAuthErrorFlag() {
		return false;
	}

	@Override
	public boolean subscribe(ContractField contract) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unsubscribe(ContractField contract) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.BIAN;
	}

}