package art.openhe.resource

import javax.inject.Singleton
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class LoginResource : Resource {


}