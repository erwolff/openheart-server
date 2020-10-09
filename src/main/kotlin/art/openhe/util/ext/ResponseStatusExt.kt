package art.openhe.util.ext

import javax.ws.rs.core.Response


fun Response.Status.log(): String = "$statusCode ($reasonPhrase)"