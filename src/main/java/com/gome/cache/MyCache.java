package com.gome.cache;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import redis.GcacheClient;

/**
 * 底层使用Gache 来支持
 * 
 * @author chixiaoyong
 *
 */

@Resource
public class MyCache implements Cache {

	private Logger log = Logger.getLogger(MyCache.class);
	
	private GcacheClient gcacheClient;

	private String name;

	private int timeout = 60;

	public MyCache() {

	}

	public MyCache(String name) {
		this.name = name;

	}

	public MyCache(String name, int timeout) {
		this.name = name;
		this.timeout = timeout;
	}

	@Override
	public void clear() {

	}

	@Override
	public void evict(Object key) {

		if (key instanceof String) {

			gcacheClient.del(key.toString());

		} else {

			throw new RuntimeException(" gcache key  type only String" + key.toString());
		}

	}

	@Override
	public ValueWrapper get(Object key) {

		ValueWrapper valueWrapper = null;

		byte[] theValue = gcacheClient.getBytes(key.toString());

		if (theValue != null) {
			valueWrapper = new SimpleValueWrapper(SerializeUtil.unserialize(theValue));
		}

		return valueWrapper;
	}

	@Override
	public <T> T get(Object arg0, Class<T> arg1) {

		return null;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public Object getNativeCache() {

		return gcacheClient;
	}

	@Override
	public void put(Object key, Object value) {

		log.info("key = " + key);
		log.info("value = " + value);

		gcacheClient.setex(key.toString(), timeout, SerializeUtil.serialize(value));

	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
