package com.gome.cache;

import java.util.Collection;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

/**
 * 继承Spring cache，实现自己的缓存
 * 
 * @author chixiaoyong
 *
 */
public class MyCacheManager extends AbstractCacheManager {

	private Collection<? extends MyCache> caches;

	public MyCacheManager() {

	}

	public MyCacheManager(Collection<? extends MyCache> caches) {
		this.caches = caches;
	}

	@Override
	protected Collection<? extends Cache> loadCaches() {

		return caches;
	}

	public void setCaches(Collection<? extends MyCache> caches) {
		this.caches = caches;
	}

}
