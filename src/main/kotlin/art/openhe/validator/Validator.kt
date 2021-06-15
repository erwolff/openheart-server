package art.openhe.validator

/**
 * Marker interface for any request validator
 *
 * The Validators are run by the Handler layer and interact with
 * the Storage layer to ensure a request is valid and properly constructed
 *
 * Top Down Hierarchy:
 *      Resource
 *      Handler -> Validator
 *      Storage
 */
interface Validator {

    // for readability - return this object
    // when validation has passed
    val valid: Nothing?
        get() = null
}