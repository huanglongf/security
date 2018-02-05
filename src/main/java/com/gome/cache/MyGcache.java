package com.gome.cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.GcacheClient;

public class MyGcache implements Cache {

	private Logger logger = Logger.getLogger(MyGcache.class);

	private GcacheClient gcache;

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private String id;

	public MyGcache(String id) {

		if (id == null) {

			throw new RuntimeException(" cache instance need  id  ");
		}

		getGcache();

		this.id = id;
	}

	@Override
	public String getId() {

		return this.id;
	}

	@Override
	public void putObject(Object key, Object value) {
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>putObject:" + key + "=" + value);

		getGcache().set(key.toString(), SerializeUtil.serialize(value));

	}

	@Override
	public Object getObject(Object key) {

		logger.info("key= " + key.toString() + " \t gcache=" + getGcache());

		byte[] bytes = gcache.getBytes(key.toString());

		if (bytes == null) {
			return null;
		}

		return SerializeUtil.unserialize(bytes);
	}

	@Override
	public Object removeObject(Object key) {

		return gcache.expire(key.toString(), 0);
	}

	@Override
	public void clear() {

	}

	@Override
	public int getSize() {

		return 1;
	}

	@Override
	public ReadWriteLock getReadWriteLock() {

		return readWriteLock;
	}

	public GcacheClient getGcache() {

		ApplicationContext context = new ClassPathXmlApplicationContext("spring-gcache.xml");

		 gcache = (GcacheClient) context.getBean("gcache");

		
		return gcache;

	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-gcache.xml");

	GcacheClient	 gcache = (GcacheClient) context.getBean("gcache");
	}

}
