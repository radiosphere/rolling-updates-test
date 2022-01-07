package io.radiosphere.rollingupdates.redundant

import org.infinispan.configuration.cache.CacheMode
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.partitionhandling.PartitionHandling
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.Produces

@ApplicationScoped
class CacheConfiguration {

    @Produces
    @ApplicationScoped
    fun cacheConfiguration(): ConfigurationBuilder {
        val builder = ConfigurationBuilder()
        builder
            .clustering()
            .cacheMode(CacheMode.DIST_SYNC)
            .hash().numOwners(3).numSegments(20 * 12)
            .stateTransfer().fetchInMemoryState(true).awaitInitialTransfer(true)
            .partitionHandling().whenSplit(PartitionHandling.ALLOW_READ_WRITES)
            .statistics().enable()
        return builder
    }
}