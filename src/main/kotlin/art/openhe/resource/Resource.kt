package art.openhe.resource

/**
 * Marker interface for any Resource
 *
 * The Resource layer receives REST requests and serves them
 * to the Handler layer
 *
 * Top Down Hierarchy:
 *      Resource
 *      Handler -> Validator
 *      Storage
 */
interface Resource