package org.dromara.northstar.common;

import xyz.redtorch.pb.CoreField.TickField;

/**
 * TICK行情组件
 * @author KevinHuangwl
 *
 */
public interface TickDataAware {

	
	void onTick(TickField tick);
	
	
	default void endOfMarket() {
		throw new UnsupportedOperationException();
	}
}
