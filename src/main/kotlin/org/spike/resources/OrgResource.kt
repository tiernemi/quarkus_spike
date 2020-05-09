package org.spike.resources

import org.neo4j.driver.Driver
import org.spike.entities.Org
import org.spike.models.OrgModel
import java.util.concurrent.CompletionStage
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import org.neo4j.driver.Values
import java.net.URI
import org.spike.uid.OrgUid

@Path("orgs")
class OrgResource(
        private val neo4jDriver: Driver
) {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun list(): CompletionStage<Response> {
        val session = neo4jDriver.asyncSession()
        return session
                .runAsync("MATCH (o:Org) RETURN o ORDER BY o.name")
                .thenCompose { cursor -> cursor.listAsync { record -> Org.from(record.get("o").asNode()) } }
                .thenCompose { orgs -> session.closeAsync().thenApply { orgs } }
                .thenApply(Response::ok)
                .thenApply(Response.ResponseBuilder::build)
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun create(org: OrgModel): CompletionStage<Response> {
        val session = neo4jDriver.asyncSession()
        return session
                .writeTransactionAsync { tx ->
                    tx.runAsync("CREATE (o:Org {uid : \$uid, name: \$name}) RETURN o", Values.parameters(mapOf("name" to org.name, "uid" to OrgUid())))
                            .thenCompose { fn -> fn.singleAsync() }
                }
                .thenApply { record -> Org.from(record.get("o").asNode()) }
                .thenCompose { persistedOrg -> session.closeAsync().thenApply { persistedOrg } }
                .thenApply { persistedOrg ->
                    Response
                            .created(URI.create("/orgs/" + persistedOrg.uid))
                            .build()
                }
    }
}