package io.radiosphere.rollingupdates.redundant

import io.quarkus.runtime.StartupEvent
import org.infinispan.manager.EmbeddedCacheManager
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@ApplicationScoped
class CacheBoostrapper {
    val logger = Logger.getLogger(this.javaClass)

    @Inject
    lateinit var cacheManager: EmbeddedCacheManager

    @Inject
    lateinit var exactlyOnceService: SessionManager

    fun boostrapCache(@Observes e: StartupEvent) {
        val timeInMs = measureTimeMillis {
            logger.info("Bootstrapping caches")
            exactlyOnceService.prepareCaches(cacheManager)

            logger.info("Starting Cache Manager")
            cacheManager.start()
            cacheManager.cacheNames.forEach {
                cacheManager.getCache<Any, Any>(it).start()
            }

        }
        logger.infof("Started all caches, took %d ms", timeInMs)

    }
}