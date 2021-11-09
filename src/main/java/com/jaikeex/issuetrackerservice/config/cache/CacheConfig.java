package com.jaikeex.issuetrackerservice.config.cache;

import com.jaikeex.issuetrackerservice.config.properties.CacheProperties;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    private final CacheProperties properties;

    @Autowired
    public CacheConfig(CacheProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
        CacheConfiguration generalEhCache = new CacheConfiguration();
        generalEhCache.setName(properties.getEhCacheName());
        generalEhCache.setMemoryStoreEvictionPolicy(properties.getEvictionPolicy());
        generalEhCache.setMaxEntriesLocalHeap(properties.getMaxEntries());
        generalEhCache.setTimeToLiveSeconds(properties.getTimeToLiveSeconds());

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(generalEhCache);
        return net.sf.ehcache.CacheManager.newInstance(config);
    }
}

