package io.radiosphere.rollingupdates.redundant

import io.radiosphere.rollingupdates.session.PagedResponse
import org.infinispan.Cache
import org.infinispan.commons.api.CacheContainerAdmin
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.manager.EmbeddedCacheManager
import org.jboss.logging.Logger
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class SessionManager() {

    val logger = Logger.getLogger(this.javaClass)

    @Inject
    lateinit var cacheManager: EmbeddedCacheManager

    @Inject
    lateinit var builder: ConfigurationBuilder

    val startSessionCache: Cache<String, Instant> by lazy {
        cacheManager.getCache("start-session")
    }

    fun startService(sessionId: String) {
        val myCache = startSessionCache
        try {
            myCache.put(sessionId, Instant.now())
        } catch ( t : Throwable) {
            logger.errorf(t, "Failed to put session %s", sessionId)
        }
    }

    fun killSessions(count: Int) {
        val totalCount = startSessionCache.count()

        val toDelete = Math.min(count, totalCount)

        val keysToDelete = startSessionCache.keys.take(toDelete)

        for (key in keysToDelete) {
            startSessionCache.remove(key)
        }
    }

    fun getAllSessions(): PagedResponse<String> {
        val count = startSessionCache.count()

        val someKeys = startSessionCache.keys.take(500)

        return PagedResponse(count, someKeys)
    }

    fun killService(sessionId: String) {
        startSessionCache.remove(sessionId)
    }

    fun prepareCaches(cacheManager: EmbeddedCacheManager) {
        logger.info("Called prepare caches!")

        cacheManager.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE)
            .getOrCreateCache<String, Instant>("start-session", builder.build())
    }
}