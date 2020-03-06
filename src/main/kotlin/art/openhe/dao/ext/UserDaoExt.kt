package art.openhe.dao.ext

import art.openhe.dao.UserDao
import art.openhe.model.User
import art.openhe.util.MongoQueryDefs
import art.openhe.util.ext.letAsObjectId
import art.openhe.util.ext.letIfNotEmpty
import art.openhe.util.ext.runQuery


fun UserDao.findByGoogleId(googleId: String): User? =
    googleId.letIfNotEmpty {
        collection.runQuery { it.findOne(MongoQueryDefs.Users.byGoogleId, googleId).`as`(User::class.java) }
    }

fun UserDao.findByEmail(email: String): User? =
    email.letIfNotEmpty {
        collection.runQuery { it.findOne(MongoQueryDefs.Users.byEmail, email).`as`(User::class.java) }
    }


fun UserDao.findOneByLastReceivedLetterTimestampLessThan(excludeId: String, timestamp: Long): User? =
    excludeId.letAsObjectId { oid ->
        collection.runQuery { it.findOne(MongoQueryDefs.Users.byIdNeAndLastReceivedLetterTimestampLt, oid, timestamp).`as`(User::class.java) }
    }