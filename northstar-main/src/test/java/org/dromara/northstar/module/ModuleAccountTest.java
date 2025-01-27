package org.dromara.northstar.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dromara.northstar.common.constant.ClosingPolicy;
import org.dromara.northstar.common.constant.ModuleState;
import org.dromara.northstar.common.model.ContractSimpleInfo;
import org.dromara.northstar.common.model.Identifier;
import org.dromara.northstar.common.model.ModuleAccountDescription;
import org.dromara.northstar.common.model.ModuleAccountRuntimeDescription;
import org.dromara.northstar.common.model.ModuleDescription;
import org.dromara.northstar.common.model.ModulePositionDescription;
import org.dromara.northstar.common.model.ModuleRuntimeDescription;
import org.dromara.northstar.data.IModuleRepository;
import org.dromara.northstar.gateway.Contract;
import org.dromara.northstar.gateway.IContractManager;
import org.dromara.northstar.strategy.IModuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import test.common.TestFieldFactory;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

class ModuleAccountTest {
	
	TestFieldFactory factory = new TestFieldFactory("testAccount");
	
	TradeField trade = factory.makeTradeField("rb2205", 1000, 2, DirectionEnum.D_Buy, OffsetFlagEnum.OF_Open);
	TradeField trade2 = factory.makeTradeField("rb2205", 1000, 2, DirectionEnum.D_Sell, OffsetFlagEnum.OF_Open);
	TradeField closeTrade = factory.makeTradeField("rb2205", 1200, 2, DirectionEnum.D_Sell, OffsetFlagEnum.OF_Close);
	
	TickField tick = factory.makeTickField("rb2205", 1000);
	
	IModuleRepository moduleRepo = mock(IModuleRepository.class);
	IModuleContext ctx = mock(IModuleContext.class);
	
	ContractSimpleInfo csi = ContractSimpleInfo.builder()
			.unifiedSymbol("rb2205@SHFE@FUTURES")
			.value("rb2205@SHFE@FUTURES@testAccount")
			.build();
	
	ModuleAccount macc;
	
	@BeforeEach
	void prepare() {
		Map<String, ModuleAccountRuntimeDescription> mamap = new HashMap<>();
		ModuleAccountDescription mad = ModuleAccountDescription.builder()
				.accountGatewayId("testAccount")
				.bindedContracts(List.of(csi))
				.build();
		
		ModuleDescription md = ModuleDescription.builder()
				.closingPolicy(ClosingPolicy.FIRST_IN_FIRST_OUT)
				.moduleAccountSettingsDescription(List.of(mad))
				.build();
		
		ModuleAccountRuntimeDescription mard = ModuleAccountRuntimeDescription.builder()
				.accountId("testAccount")
				.initBalance(100000)
				.accCloseProfit(200)
				.accCommission(10)
				.accDealVolume(3)
				.positionDescription(ModulePositionDescription.builder()
						.nonclosedTrades(List.of(trade.toByteArray(), trade2.toByteArray()))
						.build())
				.build();
		mamap.put(mard.getAccountId(), mard);
		
		ModuleRuntimeDescription mrd = ModuleRuntimeDescription.builder()
				.moduleName("testModule")
				.enabled(true)
				.moduleState(ModuleState.HOLDING_LONG)
				.accountRuntimeDescriptionMap(mamap)
				.build();
		IContractManager contractMgr = mock(IContractManager.class);
		Contract c = mock(Contract.class);
		when(c.contractField()).thenReturn(trade.getContract());
		when(contractMgr.getContract(any(Identifier.class))).thenReturn(c);
		when(ctx.getLogger()).thenReturn(mock(Logger.class));
		macc = new ModuleAccount(md, mrd, new ModuleStateMachine(ctx), moduleRepo, contractMgr, mock(Logger.class));
	}

	@Test
	void testGetModuleStateMachine() {
		assertThat(macc.getModuleState()).isEqualTo(ModuleState.EMPTY_HEDGE);
	}

	@Test
	void testOnTrade() {
		macc.onTrade(closeTrade);
		assertThat(macc.getModuleState()).isEqualTo(ModuleState.HOLDING_SHORT);
		assertThat(macc.getInitBalance("testAccount")).isEqualTo(100000);
		assertThat(macc.getAccCloseProfit("testAccount")).isEqualTo(4200);
		assertThat(macc.getAccDealVolume("testAccount")).isEqualTo(5);
		assertThat(macc.getNonclosedTrades("testAccount")).hasSize(1);
	}

	@Test
	void testGetInitBalance() {
		assertThat(macc.getInitBalance("testAccount")).isEqualTo(100000);
	}

	@Test
	void testGetUncloseTrades() {
		assertThat(macc.getNonclosedTrades("testAccount")).hasSize(2);
	}

	@Test
	void testGetAccDealVolume() {
		assertThat(macc.getAccDealVolume("testAccount")).isEqualTo(3);
	}

	@Test
	void testGetAccCloseProfit() {
		assertThat(macc.getAccCloseProfit("testAccount")).isEqualTo(200);
	}

	@Test
	void testGetPositions() {
		macc.onTick(tick);
		assertThat(macc.getPositions("testAccount")).hasSize(2);
	}

}
