package com.scanales;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/sessions")
public class SessionResource {

    @Inject
    PlayerSocket playerSocket;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listSessions() {
        Map<String, String> sessions = playerSocket.getSessionInfo();
        return Response.ok(sessions).build(); // Force JSON encoding
    }
}