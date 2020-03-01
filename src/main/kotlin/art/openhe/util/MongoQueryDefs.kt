package art.openhe.util


object MongoQueryDefs {

    /**
     * General
     */
    val updatePrefix = "{ \$set: { "
    val updatePostfix = " } }"

    /**
     * Users Queries
     */
    object Users {
        val byEmail = "{ email: # }"
        val byIdNeAndLastReceivedMessageTimestampLt = and(
            "{ _id: { \$ne: # } }",
            "{ lastReceivedMessageTimestamp: { \$lt: # } }"
        )
    }

    private fun and(vararg strings: String) =
        "{ \$and: [ " + strings.joinToString { it } + " ] }"


}