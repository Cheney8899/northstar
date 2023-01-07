package tech.quantit.northstar.gateway.tiger;

import java.util.Objects;

import com.tigerbrokers.stock.openapi.client.https.domain.contract.item.ContractItem;

import tech.quantit.northstar.common.constant.ChannelType;
import tech.quantit.northstar.common.model.Identifier;
import tech.quantit.northstar.gateway.api.domain.contract.ContractDefinition;
import tech.quantit.northstar.gateway.api.domain.contract.Instrument;
import tech.quantit.northstar.gateway.api.domain.time.TradeTimeDefinition;
import tech.quantit.northstar.gateway.tiger.time.CnStockTradeTime;
import tech.quantit.northstar.gateway.tiger.time.HkStockTradeTime;
import tech.quantit.northstar.gateway.tiger.time.UsStockTradeTime;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;
import xyz.redtorch.pb.CoreEnum.ExchangeEnum;
import xyz.redtorch.pb.CoreEnum.ProductClassEnum;
import xyz.redtorch.pb.CoreField.ContractField;

public class TigerContractAdapter implements Instrument{

	private ContractItem item; 
	
	private ContractDefinition contractDef;
	
	public TigerContractAdapter(ContractItem item) {
		this.item = item;
	}
	
	@Override
	public String name() {
		return item.getName();
	}

	@Override
	public Identifier identifier() {
		return Identifier.of(String.format("%s@%s@%s@%s", item.getSymbol(), exchange(), productClass(), channelType()));
	}

	@Override
	public ProductClassEnum productClass() {
		return switch(item.getSecType()) {
		case "STK" -> ProductClassEnum.EQUITY;
		case "OPT" -> ProductClassEnum.OPTION;
		case "FUT" -> ProductClassEnum.FUTURES;
		case "WAR" -> ProductClassEnum.WARRANTS;
		case "IOPT" -> ProductClassEnum.SPOTOPTION;
		default -> throw new IllegalArgumentException("Unexpected value: " + item.getSecType());
		};
	}

	@Override
	public ExchangeEnum exchange() {
		return switch(item.getExchange()) {
		case "SMART","VALUE" -> ExchangeEnum.SMART;
		case "SEHKSZSE" -> ExchangeEnum.SZSE;
		case "SEHKNTL" -> ExchangeEnum.SSE;
		case "SEHK" -> ExchangeEnum.SEHK;
		default -> throw new IllegalArgumentException("Unexpected value: " + item.getExchange());
		};
	}

	@Override
	public TradeTimeDefinition tradeTimeDefinition() {
		if(Objects.isNull(contractDef)) {
			throw new IllegalStateException(identifier().value() + " 没有合约定义信息");
		}
		return switch(contractDef.getTradeTimeType()) {
		case "CN_STK_TT" -> new CnStockTradeTime();
		case "HK_STK_TT" -> new HkStockTradeTime();
		case "US_STK_TT" -> new UsStockTradeTime();
		default -> throw new IllegalArgumentException("Unexpected value: " + contractDef.getTradeTimeType());
		};
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.TIGER;
	}

	@Override
	public ContractField contractField() {
		return ContractField.newBuilder()
				.setGatewayId("TIGER")
				.setSymbol(item.getSymbol())
				.setUnifiedSymbol(String.format("%s@%s@%s", item.getSymbol(), exchange(), productClass()))
				.setName(item.getName())
				.setFullName(item.getName())
				.setCurrency(CurrencyEnum.valueOf(item.getCurrency()))
				.setExchange(exchange())
				.setProductClass(productClass())
				.setContractId(identifier().value())
				.setMultiplier(item.getMultiplier())
				.setPriceTick(item.getMinTick())
				.setLongMarginRatio(item.getLongInitialMargin())
				.setShortMarginRatio(item.getShortInitialMargin())
				.setLastTradeDateOrContractMonth(item.getContractMonth())
				.setStrikePrice(item.getStrike())
				.setThirdPartyId(String.format("%s@TIGER", item.getSymbol()))
				.setCommissionFee(contractDef.getCommissionFee())
				.setCommissionRate(contractDef.getCommissionRate())
				.build();
	}

	@Override
	public void setContractDefinition(ContractDefinition contractDef) {
		this.contractDef = contractDef;
	}
	
}
