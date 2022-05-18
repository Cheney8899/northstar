package tech.quantit.northstar.domain.gateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import tech.quantit.northstar.common.constant.Constants;
import tech.quantit.northstar.common.exception.NoSuchElementException;
import tech.quantit.northstar.common.utils.ContractUtils;
import xyz.redtorch.pb.CoreField.ContractField;

/**
 * 合约管理器
 * 合约数据要么从账户网关加载，要么从本地数据库加载，加载后在程序运行时不会再改变
 * @author KevinHuangwl
 *
 */
@Slf4j
public class ContractManager {
	
	private static final int DEFAULT_SIZE = 4096;
	
	/**
	 * gateway -> unifiedSymbol -> contract
	 */
	private Map<String, Map<String, ContractField>> contractTbl = new ConcurrentHashMap<>();
	
	public ContractManager(List<ContractField> contracts) {
		for(ContractField c : contracts) {			
			addContract(c);
		}
	}
	
	public boolean addContract(ContractField contract) {
		String gatewayId = contract.getGatewayId();
		String symbol = contract.getSymbol();
		String unifiedSymbol = contract.getUnifiedSymbol();
		if(!contractTbl.containsKey(gatewayId)) {			
			contractTbl.putIfAbsent(gatewayId, new ConcurrentHashMap<>(DEFAULT_SIZE));
		}
		contractTbl.get(gatewayId).putIfAbsent(unifiedSymbol, contract);
		log.trace("加入合约：网关{}, 合约{}, 网关累计总合约数{}个", gatewayId, symbol, contractTbl.get(gatewayId).size());
		return true;
	}
	
	public ContractField getContract(String unifiedSymbol) {
		for(Entry<String, Map<String, ContractField>> e : contractTbl.entrySet()) {
			if(e.getValue().containsKey(unifiedSymbol)) {
				return e.getValue().get(unifiedSymbol);
			}
		}
		throw new NoSuchElementException("找不到合约：" + unifiedSymbol);
	}
	
	public Collection<ContractField> getAllContracts(){
		List<ContractField> results = new ArrayList<>();
		for(Entry<String, Map<String, ContractField>> e : contractTbl.entrySet()) {
			results.addAll(e.getValue().values());
		}
		return results;
	}
	
	public boolean isIndexContract(ContractField contract) {
		return contract.getSymbol().contains(Constants.INDEX_SUFFIX);
	}
	
	public List<ContractField> monthlyContractsOfIndex(ContractField contract){
		if(!isIndexContract(contract)) {
			throw new IllegalArgumentException("此合约非指数合约：" + contract.getUnifiedSymbol());
		}
		return ContractUtils.getMonthlyUnifiedSymbolOfIndexContract(contract.getUnifiedSymbol(), contract.getExchange())
				.stream()
				.map(this::getContract)
				.filter(Objects::nonNull)
				.toList();
	}
	
}
