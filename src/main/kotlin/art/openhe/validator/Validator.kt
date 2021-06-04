package art.openhe.validator

/**
 * Marker interface for any request validator
 *
 * The Validators are run by the Handler layer to ensure a
 * request is properly constructed
 *
 * Top Down Hierarchy:
 *      Resource
 *      Handler -> Validator
 *      Dao
 */
interface Validator {

    // for readability - return this object
    // when validation has passed
    val valid: Nothing?
        get() = null
}