package io.radiosphere.rollingupdates.session

import io.radiosphere.rollingupdates.redundant.SessionManager
import javax.inject.Inject
import javax.ws.rs.*

@Path("/sessions")
class SessionController {

    @Inject
    lateinit var exactlyOnceService: SessionManager

    val logger = org.jboss.logging.Logger.getLogger(this.javaClass)

    @GET
    fun getSessionsPages(): PagedResponse<String> {
        logger.debug("Controller: Called getAllSessions")
        return exactlyOnceService.getAllSessions()
    }

    @PUT
    @Path("{id}")
    fun startSession(@PathParam("id") sessionId: String) {
        logger.debugf("Controller: Called startSession %s", sessionId)
        exactlyOnceService.startService(sessionId)
    }

    @DELETE
    @Path("{id}")
    fun deleteSession(@PathParam(value = "id") sessionId: String) {
        logger.debugf("Controller: Called deleteSession %s", sessionId)
        exactlyOnceService.killService(sessionId)
    }
}