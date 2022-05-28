package tech.quantit.northstar.data.mongo.po;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import tech.quantit.northstar.common.model.ModuleDescription;

/**
 * 模组配置信息
 * @author KevinHuangwl
 *
 */
@Data
@Document
public class ModuleDescriptionPO {
	@Id
	private String moduleName;
	
	private ModuleDescription moduleDescription;
	
	public static ModuleDescriptionPO convertFrom(ModuleDescription moduleSettingsDescription) {
		ModuleDescriptionPO po = new ModuleDescriptionPO();
		po.moduleName = moduleSettingsDescription.getModuleName();
		po.moduleDescription = moduleSettingsDescription;
		return po;
	}
}
