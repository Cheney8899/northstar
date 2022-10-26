package tech.quantit.northstar.gateway.api.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import tech.quantit.northstar.common.constant.DateTimeConstant;
import tech.quantit.northstar.common.constant.TickType;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.TickField;

/**
 * 1分钟Bar生成器
 * 需要考虑以下几种情况：
 * 情况一：不同的TICK每分钟反馈的TICK数据不同，例如部分合约每秒两个TICK，部分合约每秒多于两个TICK；开始计算的时间也不尽相同，例如部分合约第一个TICK是0分500毫秒，部分合约第一个TICK是0分0毫秒；
 * 情况二：假如行情运行期间断线重连，离线间隔超过原有K线，那么应该先结束原有K线，然后重新生成新K线
 * 情况三：在非行情运行时间，也有可能收到无效的TICK数据，需要做忽略处理
 */
@Slf4j
public class BarGenerator {
	
	private BarField.Builder barBuilder;
	
	private long cutoffTime;
	
	private static final GlobalCutOffTimeHelper helper = new GlobalCutOffTimeHelper();
	
	private static final AtomicInteger cnt = new AtomicInteger();

	private NormalContract contract;
	
	private Consumer<BarField> barCallBack;
	
	private ConcurrentLinkedQueue<TickField> barTicks = new ConcurrentLinkedQueue<>();
	
	private boolean marketStopped;	// 停盘标识
	
	private Timer timer = new Timer("BarTimer-" + cnt.incrementAndGet(), true);
	
	private TimerTask autoFinishingTask; 
	
	public BarGenerator(NormalContract contract, Consumer<BarField> barCallBack) {
		this.barCallBack = barCallBack;
		this.contract = contract;
		this.barBuilder = BarField.newBuilder()
				.setGatewayId(contract.contractField().getGatewayId())
				.setUnifiedSymbol(contract.unifiedSymbol());
		helper.register(this);
		autoFinishingTask = new TimerTask() {

			@Override
			public void run() {
				if(!marketStopped && System.currentTimeMillis() > cutoffTime) {					
					BarGenerator.this.finishOfBar();
					marketStopped = true;
				}
			}
			
		};
		long secondsToNextWholeMinute = System.currentTimeMillis() % 60000;
		timer.schedule(autoFinishingTask, secondsToNextWholeMinute + 5000, 60000);
	}
	
	public BarGenerator(NormalContract contract) {
		this(contract, null);
	}
	
	/**
	 * 更新Tick数据
	 * 
	 * @param tick
	 */
	public synchronized void update(TickField tick) {
		// 如果tick为空或者合约不匹配则返回
		if (tick == null || !contract.unifiedSymbol().equals(tick.getUnifiedSymbol())) {
			log.warn("合约不匹配,当前Bar合约{}", contract.unifiedSymbol());
			return;
		}
		
		// 忽略非行情数据
		if(tick.getStatus() < 1) {
			return;
		}
		
		marketStopped = false;
		 
		if(tick.getActionTimestamp() > cutoffTime) {
			long offset = 0;	// K线偏移量
			if(tick.getStatus() == TickType.PRE_OPENING_TICK.getCode()) {
				offset = 60000;	// 开盘前一分钟的TICK是盘前数据，要合并到第一个分钟K线
			}
			long barActionTime = tick.getActionTimestamp() - tick.getActionTimestamp() % 60000L + 60000 + offset; // 采用K线的收盘时间作为K线时间
			long newCutoffTime = barActionTime;
			
			if(newCutoffTime != helper.cutoffTime) {
				helper.updateCutoffTime(newCutoffTime);
			}
			cutoffTime = newCutoffTime;
			barBuilder = BarField.newBuilder()
					.setGatewayId(contract.contractField().getGatewayId())
					.setUnifiedSymbol(contract.unifiedSymbol())
					.setTradingDay(tick.getTradingDay())
					.setOpenPrice(tick.getLastPrice())
					.setHighPrice(tick.getLastPrice())
					.setLowPrice(tick.getLastPrice())
					.setPreClosePrice(tick.getPreClosePrice())
					.setPreOpenInterest(tick.getPreOpenInterest())
					.setPreSettlePrice(tick.getPreSettlePrice())
					.setActionTimestamp(barActionTime)
					.setActionDay(tick.getActionDay())
					.setActionTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(barActionTime), ZoneId.systemDefault()).format(DateTimeConstant.T_FORMAT_WITH_MS_INT_FORMATTER));
		}
		
		barTicks.offer(tick);
		barBuilder.setHighPrice(Math.max(tick.getLastPrice(), barBuilder.getHighPrice()));
		barBuilder.setLowPrice(Math.min(tick.getLastPrice(), barBuilder.getLowPrice()));
		barBuilder.setClosePrice(tick.getLastPrice());
		barBuilder.setOpenInterest(tick.getOpenInterest());
		barBuilder.setOpenInterestDelta(tick.getOpenInterestDelta() + barBuilder.getOpenInterestDelta());
		barBuilder.setVolume(tick.getVolumeDelta() + barBuilder.getVolume());
		barBuilder.setTurnover(tick.getTurnoverDelta() + barBuilder.getTurnover());
		barBuilder.setNumTrades(tick.getNumTradesDelta() + barBuilder.getNumTrades());
	}
	
	public BarField finishOfBar() {
		if(barTicks.size() < 3) {
			// 若TICK数据少于三个TICK，则不触发回调，因为这不是一个正常的数据集
			return barBuilder.build();
		}
		barTicks.clear();
		
		barBuilder.setVolume(Math.max(0, barBuilder.getVolume()));				// 防止vol为负数
		
		BarField lastBar = barBuilder.build();
		barCallBack.accept(lastBar);
		return lastBar;
	}
	
	public void setOnBarCallback(Consumer<BarField> callback) {
		barCallBack = callback;
	}

	private static class GlobalCutOffTimeHelper{
		
		private volatile long cutoffTime;
		
		private Set<BarGenerator> registeredSet = new HashSet<>();
		
		synchronized void register(BarGenerator barGen) {
			registeredSet.add(barGen);
		}
		
		synchronized void updateCutoffTime(long newCutoffTime) {
			if(newCutoffTime > cutoffTime) {
				registeredSet.stream().forEach(BarGenerator::finishOfBar);
				LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(newCutoffTime), ZoneId.systemDefault());
				log.trace("下次K线生成时间：{}", ldt.toLocalTime());
			}
			cutoffTime = newCutoffTime;
		}
	}
}
