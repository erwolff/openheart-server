package art.openhe.dao.ext

import art.openhe.dao.UserDao
import art.openhe.model.User
import art.openhe.util.MongoQueryDefs
import art.openhe.util.ext.letAsObjectId
import art.openhe.util.ext.runQuery


fun UserDao.findByEmail(email: String): User? =
    collection.runQuery { it.findOne(MongoQueryDefs.Users.byEmail, email).`as`(User::class.java) }



fun UserDao.findOneByLastReceivedMessageTimestampLessThan(excludeId: String, timestamp: Long): User? =
    excludeId.letAsObjectId { oid ->
        collection.runQuery { it.findOne(MongoQueryDefs.Users.byIdNeAndLastReceivedMessageTimestampLt, oid, timestamp).`as`(User::class.java) }
    }