package tech.xuanwu.northstar.domain;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.exception.NoSuchElementException;
import tech.xuanwu.northstar.common.utils.ContractNameResolver;
import xyz.redtorch.pb.CoreEnum.ProductClassEnum;
import xyz.redtorch.pb.CoreField.ContractField;

@Slf4j
public class ContractManager {
	
	private static final int DEFAULT_SIZE = 15000;
	
	/**
	 * gateway -> symbol -> contract
	 */
	private Table<String, String, ContractField> contractTbl = HashBasedTable.create();
//	/**
//	 * gateway -> symbolGroup -> contractList
//	 */
//	private Table<String, String, List<ContractField>> contractGroupTbl = HashBasedTable.create();
	/**
	 * unifiedSymbol -> contract
	 */
	private Map<String, WeakReference<ContractField>> contractMap = new HashMap<>(DEFAULT_SIZE);
	private List<WeakReference<ContractField>> contractList = new LinkedList<>();
	
	
	private Set<ProductClassEnum> canHandleTypes = new HashSet<>();
	public ContractManager(String... contractTypes) {
		for(String type : contractTypes) {
			canHandleTypes.add(ProductClassEnum.valueOf(ProductClassEnum.class, type));
		}
	}
	
	public synchronized boolean addContract(ContractField contract) {
		if(!canHandleTypes.contains(contract.getProductClass())) {
			return false;
		}
		String gatewayId = contract.getGatewayId();
		String symbol = contract.getSymbol();
		String unifiedSymbol = contract.getUnifiedSymbol();
//		groupContract(contract);
		WeakReference<ContractField> ref = new WeakReference<>(contract);
		contractMap.put(unifiedSymbol, ref);
		contractTbl.put(gatewayId, symbol, contract);
		contractList.add(ref);
		log.info("加入合约：网关{}, 合约{}, 累计总合约数{}个", gatewayId, symbol, contractList.size());
		return true;
	}
	
//	private void groupContract(ContractField contract) {
//		String gatewayId = contract.getGatewayId();
//		String symbol = contract.getSymbol();
//		String symbolGroup = ContractNameResolver.symbolToSymbolGroup(symbol);
//		if(!contractGroupTbl.contains(gatewayId, symbolGroup)) {
//			contractGroupTbl.put(gatewayId, symbolGroup, new LinkedList<>());
//		}
//		contractGroupTbl.get(gatewayId, symbolGroup).add(contract);
//	}
	
//	public List<ContractField> getContractsByGroup(String gatewayId, String symbolGroup){
//		return contractGroupTbl.get(gatewayId, symbolGroup);
//	}
//	
//	public List<String> getContractGroup(String gatewayId){
//		return contractGroupTbl.row(gatewayId)
//				.keySet()
//				.stream()
//				.collect(Collectors.toList());
//	}
	
	public ContractField getContract(String gatewayId, String symbol) {
		ContractField result = contractTbl.get(gatewayId, symbol);
		if(result == null) {
			throw new NoSuchElementException("找不到合约：" + gatewayId + "_" + symbol);
		}
		return result;
	}
	
	public ContractField getContract(String unifiedSymbol) {
		ContractField result = contractMap.get(unifiedSymbol).get();
		if(result == null) {
			throw new NoSuchElementException("找不到合约：" + unifiedSymbol);
		}
		return result;
	}
	
	public Collection<ContractField> getAllContracts(){
		return contractList.stream()
				.filter(i -> i.get() != null)
				.map(i -> i.get())
				.collect(Collectors.toList());
	}
	
	public Map<String, ContractField> getContractMapByGateway(String gatewayId){
		return contractTbl.row(gatewayId);
	}
	
	public void clear(String gatewayId) {
		Map<String, ContractField> gatewayContractMap = getContractMapByGateway(gatewayId);
		for(Entry<String, ContractField> e : gatewayContractMap.entrySet()) {
			contractTbl.remove(gatewayId, e.getKey());
		}
	}
	
}