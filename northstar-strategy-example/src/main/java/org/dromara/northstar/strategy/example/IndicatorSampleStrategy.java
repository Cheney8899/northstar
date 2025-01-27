package org.dromara.northstar.strategy.example;

import org.dromara.northstar.common.constant.FieldType;
import org.dromara.northstar.common.constant.SignalOperation;
import org.dromara.northstar.common.model.DynamicParams;
import org.dromara.northstar.common.model.Setting;
import org.dromara.northstar.indicator.Indicator;
import org.dromara.northstar.indicator.model.Configuration;
import org.dromara.northstar.indicator.trend.EMAIndicator;
import org.dromara.northstar.indicator.trend.MACDIndicator;
import org.dromara.northstar.strategy.AbstractStrategy;
import org.dromara.northstar.strategy.StrategicComponent;
import org.dromara.northstar.strategy.TradeStrategy;
import org.dromara.northstar.strategy.constant.PriceType;
import org.dromara.northstar.strategy.model.TradeIntent;

import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.TickField;

/**
 * 本示例用于展示一个带指标的策略
 * 采用的是简单的均线策略：快线在慢线之上做多，快线在慢线之下做空
 *
 * ## 风险提示：该策略仅作技术分享，据此交易，风险自担 ##
 * @author KevinHuangwl
 *
 */
@StrategicComponent(IndicatorSampleStrategy.NAME)
public class IndicatorSampleStrategy extends AbstractStrategy	// 为了简化代码，引入一个通用的基础抽象类
		implements TradeStrategy{

	protected static final String NAME = "示例-指标策略";

	private InitParams params;	// 策略的参数配置信息

	private Indicator fastLine;

	private Indicator slowLine;

	private Indicator macdDiff;

	private Indicator macdDea;

	@Override
	public void onMergedBar(BarField bar) {
		log.debug("{} K线数据： 开 [{}], 高 [{}], 低 [{}], 收 [{}]",
				bar.getUnifiedSymbol(), bar.getOpenPrice(), bar.getHighPrice(), bar.getLowPrice(), bar.getClosePrice());
		// 确保指标已经准备好再开始交易
		if(!fastLine.isReady() || !slowLine.isReady()) {
			log.debug("指标未准备就绪");
			return;
		}
		switch (ctx.getState()) {
			case EMPTY -> {
				// 快线在慢线之上开多，快线在慢线之下开空
				if(shouldBuy()) {
					ctx.submitOrderReq(TradeIntent.builder()
							.contract(ctx.getContract(bar.getUnifiedSymbol()))
							.operation(SignalOperation.BUY_OPEN)
							.priceType(PriceType.OPP_PRICE)
							.volume(1)
							.timeout(5000)
							.build());
					log.info("多开");
				}
				if(shouldSell()) {
					ctx.submitOrderReq(TradeIntent.builder()
							.contract(ctx.getContract(bar.getUnifiedSymbol()))
							.operation(SignalOperation.SELL_OPEN)
							.priceType(PriceType.OPP_PRICE)
							.volume(1)
							.timeout(5000)
							.build());
					log.info("空开");
				}

			}
			case HOLDING_LONG -> {
				if(fastLine.value(0) < slowLine.value(0)) {
					ctx.submitOrderReq(TradeIntent.builder()
							.contract(ctx.getContract(bar.getUnifiedSymbol()))
							.operation(SignalOperation.SELL_CLOSE)
							.priceType(PriceType.OPP_PRICE)
							.volume(1)
							.timeout(5000)
							.build());
					log.info("平多");
				}
			}
			case HOLDING_SHORT -> {
				if(fastLine.value(0) > slowLine.value(0)) {
					ctx.submitOrderReq(TradeIntent.builder()
							.contract(ctx.getContract(bar.getUnifiedSymbol()))
							.operation(SignalOperation.BUY_CLOSE)
							.priceType(PriceType.OPP_PRICE)
							.volume(1)
							.timeout(5000)
							.build());
					log.info("平空");
				}
			}
			default -> { /* 其他情况不处理 */}
		}
	}

	@Override
	public void onTick(TickField tick) {
		log.info("时间：{} {} 价格：{} 指标值：{}", tick.getActionDay(), tick.getActionTime(), tick.getLastPrice(), fastLine.value(0));
	}

	private boolean shouldBuy() {
		return fastLine.value(0) > slowLine.value(0) && this.macdDiff.value(0) > this.macdDea.value(0);
	}

	private boolean shouldSell() {
		return fastLine.value(0) < slowLine.value(0) && this.macdDiff.value(0) < this.macdDea.value(0);
	}

	@Override
	public DynamicParams getDynamicParams() {
		return new InitParams();
	}

	@Override
	public void initWithParams(DynamicParams params) {
		this.params = (InitParams) params;
	}

	@Override
	protected void initIndicators() {
		ContractField c = ctx.getContract(params.indicatorSymbol);
		// 指标的创建
		this.fastLine = new EMAIndicator(Configuration.builder()
				.contract(c)
				.indicatorName("EMA10")
				.numOfUnits(ctx.numOfMinPerMergedBar()).build(), 10);
		this.slowLine = new EMAIndicator(Configuration.builder()
				.contract(c)
				.indicatorName("EMA20")
				.numOfUnits(ctx.numOfMinPerMergedBar()).build(), 20);
		MACDIndicator macd = new MACDIndicator(Configuration.builder()
				.contract(c)
				.indicatorName("MACD")
				.numOfUnits(ctx.numOfMinPerMergedBar())
				.build(), 12, 26, 9);
		this.macdDiff = macd.getDiffLine();
		this.macdDea = macd.getDeaLine();

		// 指标的注册
		ctx.registerIndicator(fastLine);
		ctx.registerIndicator(slowLine);
		ctx.registerIndicator(macd);
	}

	public static class InitParams extends DynamicParams {			
		
		@Setting(label="指标合约", order=0)
		private String indicatorSymbol;
		
		@Setting(label="快线周期", type = FieldType.NUMBER, order=1)		
		private int fast;						
		
		@Setting(label="慢线周期", type = FieldType.NUMBER, order=2)		
		private int slow;

	}

}
