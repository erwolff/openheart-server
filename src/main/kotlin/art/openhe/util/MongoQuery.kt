package art.openhe.util


object MongoQuery {

    /**
     * General
     */
    val updatePrefix = "{ \$set: { "
    val updatePostfix = " } }"

    fun id(id: String) = if (id.contains("\$ne")) idNe(id) else "{ _id: { \$oid: \"$id\" } }"

    object Sort {
        val byCreatedTimestampDesc = "{ createdTimestamp: -1 }"
        val bySentTimestampDesc = "{ sentTimestamp: -1 }"
        val byWrittenTimestampDesc = "{ writtenTimestamp: -1 }"
    }

    /**
     * Users Queries
     */
    object Users {

        fun email(email: String?) = email?.let { "{ email: \"$email\" }" } ?: "{ email: null }"
        
        fun googleId(googleId: String?) = googleId?.let { "{ googleId: \"$googleId\" }" } ?: "{ googleId: null }"

        fun lastReceivedLetterTimestamp(lastReceivedLetterTimestamp: Any?) = "{ lastReceivedLetterTimestamp: $lastReceivedLetterTimestamp }"

        fun lastSentLetterTimestamp(lastSentLetterTimestamp: Any?) = "{ lastSentLetterTimestamp: $lastSentLetterTimestamp }"

        fun hearts(hearts: Any?) = "{ hearts: $hearts }"
    }

    object Letters {
        
        fun authorId(authorId: String?) = authorId?.let { "{ authorId: \"$authorId\" }" } ?: "{ authorId: null }"
        
        fun recipientId(recipientId: String?) = recipientId?.let { "{ recipientId: \"$recipientId\" }" } ?: "{ recipientId: null }"
        
        fun parentId(parentId: String?) = parentId?.let { "{ parentId: \"$parentId\" }" } ?: "{ parentId: null }"
        
        fun childId(childId: String?) = childId?.let { "{ childId: \"$childId\" }" } ?: "{ childId: null }"
        
        fun hearted(hearted: Boolean?) = "{ hearted: $hearted }"

        fun deleted(deleted: Boolean?) = "{ deleted: $deleted }"
    
    }

    fun ne(ne: Any?) =
        if (ne is String) "{ \$ne: \"$ne\" }"
        else "{ \$ne: $ne }"
    
    fun lt(lt: Number) = "{ \$lt: $lt }"

    fun gt(gt: Number) = "{ \$gt: $gt }"

    fun and(vararg strings: String) =
        "{ \$and: [ " + strings.joinToString { it } + " ] }"


    // this is a bit of a hack, but in order to use _id with the "ne" command, we have to format the query properly
    private fun idNe(id: String) = "{ _id: { \$ne: { \$oid: \"${id.substring(id.indexOf("\$ne:").plus(6), id.lastIndexOf("}").minus(2))}\" } } }"
}