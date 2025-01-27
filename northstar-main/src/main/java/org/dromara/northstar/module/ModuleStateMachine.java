package org.dromara.northstar.module;

import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.dromara.northstar.common.TransactionAware;
import org.dromara.northstar.common.constant.ModuleState;
import org.dromara.northstar.common.utils.OrderUtils;
import org.dromara.northstar.strategy.IModuleAccount;
import org.dromara.northstar.strategy.IModuleContext;

import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 模组状态机
 * 负责计算模组状态
 * @author KevinHuangwl
 *
 */
public class ModuleStateMachine implements TransactionAware {
	
	private ModuleState curState = ModuleState.EMPTY;
	
	private ModuleState prevState;

	private IModuleAccount moduleAccount;
	
	private IModuleContext ctx;
	
	public ModuleStateMachine(IModuleContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void onOrder(OrderField order) {
		if(!curState.isOrdering()) {
			throw new IllegalStateException("当前状态异常：" + curState);
		}
		if(curState.isOrdering() && !OrderUtils.isValidOrder(order)) {
			setState(prevState);
		}
		if(OrderUtils.isValidOrder(order) && order.getOrderStatus() != OrderStatusEnum.OS_AllTraded) {
			setState(ModuleState.PENDING_ORDER);
		}
	}

	@Override
	public void onTrade(TradeField trade) {
		if(trade.getDirection() == DirectionEnum.D_Unknown) {
			throw new IllegalArgumentException("成交方向不明确");
		}
		if(trade.getOffsetFlag() == OffsetFlagEnum.OF_Unknown) {
			throw new IllegalArgumentException("操作意图不明确");
		}
		updateState();
	}
	
	private void updateState() {
		List<TradeField> nonclosedTrade = moduleAccount.getNonclosedTrades();
		if(nonclosedTrade.isEmpty()) {
			setState(ModuleState.EMPTY);
		} else {
			int buyPos = nonclosedTrade.stream().filter(t -> t.getDirection() == DirectionEnum.D_Buy).mapToInt(TradeField::getVolume).sum();
			int sellPos = nonclosedTrade.stream().filter(t -> t.getDirection() == DirectionEnum.D_Sell).mapToInt(TradeField::getVolume).sum();
			if(buyPos * sellPos > 0) {
				TradeField longTrade = nonclosedTrade.stream().filter(t -> t.getDirection() == DirectionEnum.D_Buy).toList().get(0);
				TradeField shortTrade = nonclosedTrade.stream().filter(t -> t.getDirection() == DirectionEnum.D_Sell).toList().get(0);
				if(StringUtils.equals(longTrade.getContract().getContractId(), shortTrade.getContract().getContractId()) && buyPos == sellPos) {
					setState(ModuleState.EMPTY_HEDGE);
				} else {
					setState(ModuleState.HOLDING_HEDGE);
				}
			} else if(buyPos > 0) {
				setState(ModuleState.HOLDING_LONG);
			} else if(sellPos > 0) {
				setState(ModuleState.HOLDING_SHORT);
			}
		}
	}
	
	public ModuleState getState() {
		return curState;
	}
	
	private void setState(ModuleState newState) {
		ctx.getLogger().info("状态机切换：[{}] => [{}]", curState, newState);
		this.curState = newState;
	}

	public void onSubmitReq(SubmitOrderReqField orderReq) {
		if(curState.isOrdering()) {
			throw new IllegalStateException("当前状态异常：" + curState);
		}
		prevState = curState;
		setState(ModuleState.PLACING_ORDER);
	}

	public void onCancelReq(CancelOrderReqField cancelReq) {
		if(!curState.isOrdering()) {
			throw new IllegalStateException("当前状态异常：" + curState);
		}
		setState(ModuleState.RETRIEVING_FOR_CANCEL);
	}
	
	public void setModuleAccount(IModuleAccount moduleAccount) {
		this.moduleAccount = moduleAccount;
		updateState();
	}

}
