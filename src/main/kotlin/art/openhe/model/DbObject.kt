package art.openhe.model

import org.joda.time.DateTimeUtils


abstract class DbObject {

    abstract val id: String

    val createdTimestamp: Long = DateTimeUtils.currentTimeMillis()

}
