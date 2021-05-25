package cn.nwcdcloud.samples.ocr.config;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class CacheConfig {
	private CacheManager cacheManager;

	@Bean
	public CacheManager cacheManager() {
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
		return cacheManager;
	}

	@DependsOn("cacheManager")
	@Bean
	public Cache<String, Object> cacheImageByte() {
		CacheConfigurationBuilder<String, Object> cacheConfig = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, Object.class,
						ResourcePoolsBuilder.newResourcePoolsBuilder().heap(20, EntryUnit.ENTRIES))
				.withDispatcherConcurrency(4);
		return cacheManager.createCache("cacheImageByte", cacheConfig);
	}
}
