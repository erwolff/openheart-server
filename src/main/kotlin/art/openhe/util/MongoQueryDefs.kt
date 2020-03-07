package art.openhe.util


object MongoQueryDefs {

    /**
     * General
     */
    val updatePrefix = "{ \$set: { "
    val updatePostfix = " } }"

    object Sort {
        val byCreatedTimestampDesc = "{ createdTimestamp: -1 }"
    }

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

    object Letters {
        val byAuthorId = "{ authorId: # }"
    }

    private fun and(vararg strings: String) =
        "{ \$and: [ " + strings.joinToString { it } + " ] }"


}