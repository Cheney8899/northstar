package tech.quantit.northstar.gateway.api.domain.mktdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import tech.quantit.northstar.common.constant.DateTimeConstant;
import tech.quantit.northstar.gateway.api.domain.time.GenericTradeTime;
import test.common.TestFieldFactory;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.TickField;

class MinuteBarGeneratorTest {

	TestFieldFactory factory = new TestFieldFactory("testGateway");
	
	@SuppressWarnings("unchecked")
	@Test
	void test() {
		ContractField contract = factory.makeContract("rb2210");
		MinuteBarGenerator gen = new MinuteBarGenerator(contract, new GenericTradeTime(), mock(Consumer.class));
		long now = System.currentTimeMillis();
		long expectedTime = now - now % 60000 + 60000;
		LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(expectedTime), ZoneId.systemDefault());
		System.out.println(ldt.toLocalTime().format(DateTimeConstant.T_FORMAT_FORMATTER));
		TickField tick1 = factory.makeTickField("rb2210", 1000);
		
		gen.update(tick1);
		
		BarField bar = gen.finishOfBar();
		assertThat(bar.getActionTimestamp()).isEqualTo(expectedTime);
		assertThat(bar.getActionTime()).isEqualTo(ldt.toLocalTime().format(DateTimeConstant.T_FORMAT_FORMATTER));
	}

}