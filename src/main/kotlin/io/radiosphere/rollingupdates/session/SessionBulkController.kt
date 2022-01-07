package io.radiosphere.rollingupdates.session

import io.radiosphere.rollingupdates.redundant.SessionManager
import java.util.*
import javax.inject.Inject
import javax.ws.rs.*
import kotlin.system.measureTimeMillis

@Path("/session-bulk")
class SessionBulkController {

    val logger = org.jboss.logging.Logger.getLogger(this.javaClass)

    @Inject
    lateinit var exactlyOnceService: SessionManager

    @POST
    @Path("create/{count}")
    fun startSession(@PathParam("count") count: Int) {
        logger.debugf("Controller: Called bulk start session %d", count)
        val time = measureTimeMillis {
            for (i in 1..count) {
                exactlyOnceService.startService(UUID.randomUUID().toString())
            }
        }
        logger.infof("Create bulk executed in %d millis", time)
    }

    @POST
    @Path("delete/{count}")
    fun killSessions(@PathParam("count") count: Int) {
        logger.debugf("Controller: Called bulk stop sessions %d", count)
        exactlyOnceService.killSessions(count)
    }
}