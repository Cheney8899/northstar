package org.dromara.northstar.data.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;

import org.dromara.northstar.data.jdbc.entity.BarDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MarketDataRepositoryTest {
	
	@Autowired
	MarketDataRepository repo;
	
	String unifiedSymbol = "rb2310@SHFE@FUTURES";
	
	BarDO bar1 = new BarDO(unifiedSymbol, "20230430", 1, null);
	BarDO bar2 = new BarDO(unifiedSymbol, "20230430", 2, null);
	BarDO bar3 = new BarDO(unifiedSymbol, "20230503", 3, null);
	
	@BeforeEach
	void prepare() {
		repo.saveAll(List.of(bar1, bar2, bar3));
	}
	
	@Test
	void testFindByUnifiedSymbolAndTradingDay() {
		List<BarDO> list = repo.findByUnifiedSymbolAndTradingDay(unifiedSymbol, "20230430");
		assertThat(list).hasSize(2);
		
		List<BarDO> list2 = repo.findByUnifiedSymbolAndTradingDay(unifiedSymbol, "20230503");
		assertThat(list2).hasSize(1);
	}

	@Test
	void testDeleteByExpiredAtBefore() {
		repo.deleteByExpiredAtBefore(3);
		Iterator<BarDO> itBars = repo.findAll().iterator();
		assertThat(itBars.next()).isNotNull();
		assertThat(itBars.hasNext()).isFalse();
	}

}
