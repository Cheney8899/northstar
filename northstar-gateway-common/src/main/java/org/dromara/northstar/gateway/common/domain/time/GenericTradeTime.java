package org.dromara.northstar.gateway.common.domain.time;

import java.time.LocalTime;
import java.util.List;

import org.dromara.northstar.gateway.TradeTimeDefinition;
import org.dromara.northstar.gateway.model.PeriodSegment;

public class GenericTradeTime implements TradeTimeDefinition {

	@Override
	public List<PeriodSegment> tradeTimeSegments() {
		return List.of(new PeriodSegment(LocalTime.of(0, 1), LocalTime.of(0, 0)));
	}

}
