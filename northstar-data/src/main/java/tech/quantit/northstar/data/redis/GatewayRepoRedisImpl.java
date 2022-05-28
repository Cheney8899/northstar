package tech.quantit.northstar.data.redis;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson2.JSON;

import tech.quantit.northstar.common.constant.Constants;
import tech.quantit.northstar.common.model.GatewayDescription;
import tech.quantit.northstar.data.IGatewayRepository;

/**
 * 
 * @author KevinHuangwl
 *
 */
public class GatewayRepoRedisImpl implements IGatewayRepository{
	
	private RedisTemplate<String, byte[]> redisTemplate;
	
	private static final String KEY_PREFIX = Constants.APP_NAME + "Gateway:";
	
	public GatewayRepoRedisImpl(RedisTemplate<String, byte[]> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * redis的数据保存结构
	 * key -> string
	 * key=Gateway:GatewayId
	 * value=json(object)
	 */
	@Override
	public void insert(GatewayDescription gatewayDescription) {
		Set<String> gatewayKeys = redisTemplate.keys(KEY_PREFIX + gatewayDescription.getGatewayId());
		if(!gatewayKeys.isEmpty()) {
			throw new IllegalStateException();
		}
		save(gatewayDescription);
	}

	@Override
	public void save(GatewayDescription gatewayDescription) {
		redisTemplate.boundValueOps(KEY_PREFIX + gatewayDescription.getGatewayId()).set(JSON.toJSONBytes(gatewayDescription));
	}

	@Override
	public void deleteById(String gatewayId) {
		redisTemplate.delete(KEY_PREFIX + gatewayId);
	}

	@Override
	public List<GatewayDescription> findAll() {
		Set<String> gatewayKeys = redisTemplate.keys(KEY_PREFIX+"*");
		return gatewayKeys.stream()
				.map(key -> redisTemplate.boundValueOps(key).get())
				.map(jsonb -> JSON.parseObject(jsonb, GatewayDescription.class))
				.toList();
	}

}