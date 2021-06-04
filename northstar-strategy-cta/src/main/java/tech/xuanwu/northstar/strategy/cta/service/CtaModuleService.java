package tech.xuanwu.northstar.strategy.cta.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import tech.xuanwu.northstar.strategy.common.Dealer;
import tech.xuanwu.northstar.strategy.common.RiskControlRule;
import tech.xuanwu.northstar.strategy.common.SignalPolicy;
import tech.xuanwu.northstar.strategy.common.annotation.StrategicComponent;
import tech.xuanwu.northstar.strategy.common.model.ComponentField;
import tech.xuanwu.northstar.strategy.common.model.ComponentMetaInfo;
import tech.xuanwu.northstar.strategy.common.model.CtaStrategyModule;
import tech.xuanwu.northstar.strategy.common.model.DynamicParams;
import tech.xuanwu.northstar.strategy.cta.persistence.StrategyModuleRepository;

public class CtaModuleService implements InitializingBean{
	
	private ApplicationContext ctx;
	
	private StrategyModuleRepository moduleRepo;
	
	public CtaModuleService(ApplicationContext ctx, StrategyModuleRepository moduleRepo) {
		this.ctx = ctx;
		this.moduleRepo = moduleRepo;
	}
	
	/**
	 * 查询可选的信号策略
	 * @return
	 */
	public List<ComponentMetaInfo> getRegisteredSignalPolicies(){
		return getComponentMeta(SignalPolicy.class);
	}
	
	/**
	 * 查询可选的风控规则
	 * @return
	 */
	public List<ComponentMetaInfo> getRegisteredRiskControlRules(){
		return getComponentMeta(RiskControlRule.class);
	}
	
	/**
	 * 查询可选的交易策略
	 * @return
	 */
	public List<ComponentMetaInfo> getRegisteredDealers(){
		return getComponentMeta(Dealer.class);
	}
	
	private List<ComponentMetaInfo> getComponentMeta(Class<?> componentClass){
		Map<String, Object> objMap = ctx.getBeansWithAnnotation(StrategicComponent.class);
		List<ComponentMetaInfo> result = new ArrayList<>(objMap.size());
		for(Entry<String, Object> e : objMap.entrySet()) {
			if(e.getValue().getClass().isAssignableFrom(componentClass)) {
				SignalPolicy policy = (SignalPolicy) e.getValue();
				StrategicComponent anno = policy.getClass().getAnnotation(StrategicComponent.class);
				result.add(new ComponentMetaInfo(anno.value(), policy.getClass()));
			}
		}
		return result;
	}
	
	/**
	 * 获取组件参数
	 * @param name
	 * @return
	 */
	public Map<String, ComponentField> getComponentParams(ComponentMetaInfo info){
		SignalPolicy policy = (SignalPolicy) ctx.getBean(info.getClz());
		DynamicParams params = policy.getDynamicParams();
		return params.getMetaInfo();
	}

	/**
	 * 新增模组
	 * @param module
	 * @param shouldSave
	 */
	public void createModule(CtaStrategyModule module, boolean shouldSave) {
		
		if(shouldSave) {
			moduleRepo.save(module);
		}
	}
	
	/**
	 * 更新模组
	 * @param module
	 */
	public void updateModule(CtaStrategyModule module) {
		moduleRepo.save(module);
	}
	
	/**
	 * 查询所有模组
	 * @return
	 */
	public List<CtaStrategyModule> getCurrentModules(){
		return moduleRepo.findAll();
	}
	
	/**
	 * 移除模组
	 * @param moduleName
	 */
	public void removeModule(String moduleName) {
		moduleRepo.deleteById(moduleName);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 加载已有模组
	}
}