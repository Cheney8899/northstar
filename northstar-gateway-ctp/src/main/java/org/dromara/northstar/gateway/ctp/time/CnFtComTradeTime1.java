package org.dromara.northstar.gateway.ctp.time;

import java.time.LocalTime;
import java.util.List;

import org.dromara.northstar.gateway.TradeTimeDefinition;
import org.dromara.northstar.gateway.model.PeriodSegment;

/**
 * 国内商品期货一类品种交易时间（有夜盘，23:00收盘）
 * @author KevinHuangwl
 *
 */
public final class CnFtComTradeTime1 implements TradeTimeDefinition {

	@Override
	public List<PeriodSegment> tradeTimeSegments() {
		return List.of(
				new PeriodSegment(LocalTime.of(21, 0), LocalTime.of(23, 00)),
				new PeriodSegment(LocalTime.of(9, 1), LocalTime.of(10, 15)),
				new PeriodSegment(LocalTime.of(10, 31), LocalTime.of(11, 30)),
				new PeriodSegment(LocalTime.of(13, 31), LocalTime.of(15, 00))
			);
	}

}
