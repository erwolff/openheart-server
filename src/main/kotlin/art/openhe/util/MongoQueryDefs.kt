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
        val byGoogleId = "{ googleId: # }"
        val byIdNeAndLastReceivedLetterTimestampLt = and(
            "{ _id: { \$ne: # } }",
            "{ lastReceivedLetterTimestamp: { \$lt: # } }"
        )
    }

    private fun and(vararg strings: String) =
        "{ \$and: [ " + strings.joinToString { it } + " ] }"


}