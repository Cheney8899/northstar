package org.dromara.northstar.gateway.common.domain.time;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dromara.northstar.gateway.TradeTimeDefinition;
import org.dromara.northstar.gateway.model.PeriodSegment;

/**
 * K线周期分割器
 * @author KevinHuangwl
 *
 */
public class PeriodHelper {
	
	private List<LocalTime> baseTimeFrame = new ArrayList<>(256); // 基准时间线
	private Set<LocalTime> endOfSections = new HashSet<>();
	
	public PeriodHelper(int numbersOfMinPerPeriod, TradeTimeDefinition tradeTimeDefinition) {
		this(numbersOfMinPerPeriod, tradeTimeDefinition, true);
	}
	
	public PeriodHelper(int numbersOfMinPerPeriod, TradeTimeDefinition tradeTimeDefinition, boolean exclusiveOpening) {
		List<PeriodSegment> tradeTimeSegments = tradeTimeDefinition.tradeTimeSegments();
		LocalTime opening = tradeTimeSegments.get(0).startOfSegment();
		LocalTime ending = tradeTimeSegments.get(tradeTimeSegments.size() - 1).endOfSegment();
		LocalTime t = opening.plusMinutes(1);
		if(!exclusiveOpening) {
			baseTimeFrame.add(opening);
		}
		int minCount = 1;
		while(!t.equals(opening)) {
			for(PeriodSegment ps : tradeTimeSegments) {
				endOfSections.add(ps.endOfSegment());
				while(ps.withinPeriod(t)) {
					if(!t.equals(opening) && minCount == numbersOfMinPerPeriod && !t.equals(ending)) {
						baseTimeFrame.add(t);
						minCount = 1;
					} else if (t.equals(ending)) {
						baseTimeFrame.add(t);
						return;
					} else {
						minCount++;
					}
					t = t.plusMinutes(1);
				}
			}
			t = t.plusMinutes(1);
		}
	}

	/**
	 * 获取K线时间基线
	 * @return
	 */
	public List<LocalTime> getRunningBaseTimeFrame(){
		return baseTimeFrame;
	}
	
	/**
	 * 当前时间是否要小节收盘
	 * @param t
	 * @return
	 */
	public boolean isEndOfSection(LocalTime t) {
		return endOfSections.contains(t);
	}
}
