package com.ruoyi.framework.helper;

import com.ruoyi.common.constant.CommonConstants;
import com.ruoyi.common.utils.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisHelper {

	/**
	 * 默认缓存有效期1小时
	 */
	public static final int DEFAULT_SECOND = 60 * 60 * 1;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 生成带前缀的 rediskey
	 *
	 * @param rediskeys
	 * @return
	 */
	public static String buildRediskey(String... rediskeys) {
		String key = CommonConstants.REDIS_KEY_PREFIX.concat(rediskeys[0]);
		for (int i = 1; i < rediskeys.length; i++) {
			key = key.concat(rediskeys[i]);
		}
		return key;
	}

	/**
	 * rediskey 是否存在
	 *
	 * @param rediskey
	 * @return
	 */
	public boolean exists(String rediskey) {
		return redisTemplate.hasKey(rediskey);
	}

	/**
	 * 取得序列化对象
	 *
	 * @param rediskey
	 * @param <T>
	 * @return
	 */
	public <T> T getBySerializable(String rediskey) {
		if(null != redisTemplate.opsForValue().get(rediskey)) {
			return (T) SerializeUtil.deserialize(redisTemplate.opsForValue().get(rediskey).toString());
		}else {
			return null;
		}
	}

	/**
	 * 将可序列化对象放入redis，key不存在时才能放入
	 *
	 * @param rediskey
	 * @param data
	 * @param second
	 */
	public void setNXSerializable(String rediskey, Object data, int second) {
		redisTemplate.opsForValue().setIfAbsent(rediskey, SerializeUtil.serizlize(data), second, TimeUnit.SECONDS);
	}

	/**
	 * 正则取得keys
	 *
	 * @param key
	 * @return
	 */
	public Set<String> keys(String key) {
		return redisTemplate.keys(key);
	}

	/**
	 * 删除指定key value
	 *
	 * @param key
	 */
	public void del(String key) {
		redisTemplate.delete(key);
	}

	public void del(String... keys) {
		redisTemplate.delete(keys);
	}
}
