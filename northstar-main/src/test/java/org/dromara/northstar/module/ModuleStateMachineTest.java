package org.dromara.northstar.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dromara.northstar.common.constant.Constants;
import org.dromara.northstar.common.constant.ModuleState;
import org.dromara.northstar.strategy.IModuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import test.common.TestFieldFactory;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TradeField;

class ModuleStateMachineTest {

	TestFieldFactory factory = new TestFieldFactory("testGateway");
	
	SubmitOrderReqField orderReq = factory.makeOrderReq("rb2205", DirectionEnum.D_Buy, OffsetFlagEnum.OF_Open, 2, 1000, 0);
	SubmitOrderReqField orderReq2 = factory.makeOrderReq("rb2205", DirectionEnum.D_Sell, OffsetFlagEnum.OF_Open, 2, 1000, 0);
	
	OrderField order = factory.makeOrderField("rb2205", 1000, 2, DirectionEnum.D_Buy, OffsetFlagEnum.OF_Open, OrderStatusEnum.OS_Unknown);
	OrderField order2 = factory.makeOrderField("rb2205", 1000, 2, DirectionEnum.D_Buy, OffsetFlagEnum.OF_Open, OrderStatusEnum.OS_AllTraded);
	OrderField order3 = factory.makeOrderField("rb2205", 1000, 2, DirectionEnum.D_Buy, OffsetFlagEnum.OF_Open, OrderStatusEnum.OS_Canceled);
	OrderField order4 = factory.makeOrderField("rb2205", 1000, 2, DirectionEnum.D_Sell, OffsetFlagEnum.OF_Open, OrderStatusEnum.OS_Touched);
	
	TradeField trade = factory.makeTradeField("rb2205", 1000, 2, DirectionEnum.D_Buy, OffsetFlagEnum.OF_Open);
	TradeField trade2 = factory.makeTradeField("rb2205", 1000, 2, DirectionEnum.D_Sell, OffsetFlagEnum.OF_Open);
	
	CancelOrderReqField cancelReq = factory.makeCancelReq(orderReq);
	
	IModuleContext ctx = mock(IModuleContext.class);
	
	ModuleAccount macc = mock(ModuleAccount.class);
	
	@BeforeEach
	void prepare() {
		when(ctx.getLogger()).thenReturn(mock(Logger.class));
	}
	
	@Test
	void shouldGetEmpty() {
		ModuleStateMachine msm = new ModuleStateMachine(ctx);
		assertThat(msm.getState()).isEqualTo(ModuleState.EMPTY);
	}

	@Test
	void shouldGetPlacingOrder() {
		ModuleStateMachine msm = new ModuleStateMachine(ctx);
		msm.onSubmitReq(orderReq);
		assertThat(msm.getState()).isEqualTo(ModuleState.PLACING_ORDER);
	}
	
	@Test
	void shouldGetPendingOrder() {
		ModuleStateMachine msm = new ModuleStateMachine(ctx);
		msm.onSubmitReq(orderReq);
		msm.onOrder(order);
		msm.onOrder(order2);
		assertThat(msm.getState()).isEqualTo(ModuleState.PENDING_ORDER);
	}
	
	@Test
	void shouldGetLong() {
		when(macc.getNonclosedTrades()).thenReturn(List.of(trade));
		ModuleStateMachine msm = new ModuleStateMachine(ctx);
		msm.setModuleAccount(macc);
		msm.onSubmitReq(orderReq);
		msm.onOrder(order);
		msm.onTrade(trade);
		assertThat(msm.getState()).isEqualTo(ModuleState.HOLDING_LONG);
	}
	
	@Test
	void shouldGetShort() {
		when(macc.getNonclosedTrades()).thenReturn(List.of(trade2));
		ModuleStateMachine msm = new ModuleStateMachine(ctx);
		msm.setModuleAccount(macc);
		msm.onSubmitReq(orderReq2);
		msm.onOrder(order4);
		msm.onTrade(trade2);
		assertThat(msm.getState()).isEqualTo(ModuleState.HOLDING_SHORT);
	}
	
	@Test
	void shouldGetHedgeEmpty() {
		when(macc.getNonclosedTrades()).thenReturn(List.of(trade));
		ModuleStateMachine msm = new ModuleStateMachine(ctx);
		msm.setModuleAccount(macc);
		msm.onSubmitReq(orderReq);
		msm.onOrder(order);
		msm.onTrade(trade);
		assertThat(msm.getState()).isEqualTo(ModuleState.HOLDING_LONG);
		
		when(macc.getNonclosedTrades()).thenReturn(List.of(trade, trade2));
		msm.onSubmitReq(orderReq2);
		msm.onOrder(order4);
		msm.onTrade(trade2);
		assertThat(msm.getState()).isEqualTo(ModuleState.EMPTY_HEDGE);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void shouldGetHedgeHolding() {
		TradeField trade3 = TradeField.newBuilder()
				.setOriginOrderId(Constants.MOCK_ORDER_ID)
				.setContract(trade.getContract())
				.setDirection(DirectionEnum.D_Sell)
				.setOffsetFlag(OffsetFlagEnum.OF_Open)
				.setVolume(3)
				.build();
		when(macc.getNonclosedTrades()).thenReturn(List.of(trade), List.of(trade, trade3));
		ModuleStateMachine msm = new ModuleStateMachine(ctx);
		msm.setModuleAccount(macc);
		msm.onSubmitReq(orderReq);
		msm.onOrder(order);
		msm.onTrade(trade);
		msm.onTrade(trade3);
		assertThat(msm.getState()).isEqualTo(ModuleState.HOLDING_HEDGE);
	}
	
	@Test
	void shouldGetCancelling() {
		ModuleStateMachine msm = new ModuleStateMachine(ctx);
		msm.onSubmitReq(orderReq);
		msm.onOrder(order);
		msm.onCancelReq(cancelReq);
		assertThat(msm.getState()).isEqualTo(ModuleState.RETRIEVING_FOR_CANCEL);
		msm.onOrder(order3);
		assertThat(msm.getState()).isEqualTo(ModuleState.EMPTY);
	}
}
