package io.radiosphere.rollingupdates.cache

import org.infinispan.configuration.global.GlobalConfigurationBuilder
import org.infinispan.lock.EmbeddedClusteredLockManagerFactory
import org.infinispan.lock.api.ClusteredLockManager
import org.infinispan.lock.configuration.ClusteredLockManagerConfigurationBuilder
import org.infinispan.lock.configuration.Reliability
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import org.jboss.logmanager.Logger
import java.util.concurrent.TimeUnit
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces


@ApplicationScoped
class InfinispanCacheFactory {

    val logger = Logger.getAnonymousLogger()

    @Produces
    @ApplicationScoped
    fun createCacheManager(): EmbeddedCacheManager {

        logger.info("Creating Cache Manager...")
        val global = GlobalConfigurationBuilder.defaultClusteredBuilder()

        global.addModule(ClusteredLockManagerConfigurationBuilder::class.java)
            .reliability(Reliability.AVAILABLE)

        global.transport()
            .distributedSyncTimeout(4 * 60 * 1000L)
            .initialClusterTimeout(4, TimeUnit.MINUTES)

        if(System.getenv("POD_NAME") != null) {
            global.transport().addProperty("configurationFile", "default-configs/default-jgroups-kubernetes.xml")
        }

        val cacheManager = DefaultCacheManager(global.build())

        return cacheManager
    }

}