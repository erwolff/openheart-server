package art.openhe.dao.ext

import art.openhe.dao.LetterDao
import art.openhe.model.Letter
import art.openhe.model.Page
import art.openhe.util.MongoQueryDefs
import art.openhe.util.ext.letIfNotEmpty
import art.openhe.util.ext.runQuery
import kotlin.math.ceil


fun LetterDao.findPageByAuthorId(authorId: String, page: Int, size: Int): Page<Letter>? {
    return authorId.letIfNotEmpty {
        //TODO: make this into framework
        val totalResults = collection.runQuery { it.count(MongoQueryDefs.Letters.byAuthorId, authorId) } ?: 0
        val results = collection.runQuery {
            it.find(MongoQueryDefs.Letters.byAuthorId, authorId)
                .sort(MongoQueryDefs.Sort.byWrittenTimestampDesc)
                .skip(page.minus(1) * size)
                .limit(size)
                .`as`(Letter::class.java)
        }

        results?.let {
            Page(results.toList(), page, size, totalResults, ceil(totalResults / size.toDouble()).toInt())
        }
    }
}

fun LetterDao.findPageByRecipientId(recipientId: String, page: Int, size: Int): Page<Letter>? {
    return recipientId.letIfNotEmpty {
        //TODO: make this into framework
        val totalResults = collection.runQuery { it.count(MongoQueryDefs.Letters.byRecipientId, recipientId) } ?: 0
        val results = collection.runQuery {
            it.find(MongoQueryDefs.Letters.byRecipientId, recipientId)
                .sort(MongoQueryDefs.Sort.bySentTimestampDesc)
                .skip(page.minus(1) * size)
                .limit(size)
                .`as`(Letter::class.java)
        }

        results?.let {
            Page(results.toList(), page, size, totalResults, ceil(totalResults / size.toDouble()).toInt())
        }
    }
}
