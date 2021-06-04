package art.openhe.handler

import art.openhe.model.response.EmptyResponse
import art.openhe.model.response.HandlerResult
import art.openhe.model.response.asSuccess

/**
 * Marker interface for any request handler
 *
 * The Handler layer serves requests received by the Resource layer
 * and interacts with the Validator and Dao layers (and serves responses
 * back to the Resource layer as necessary)
 *
 * Top Down Hierarchy:
 *      Resource
 *      Handler -> Validator
 *      Dao
 */
interface Handler {

    // for readability - return this object
    // when supplying a successful empty response
    val emptyResponse: HandlerResult.Success<EmptyResponse>
        get() = EmptyResponse().asSuccess()
}