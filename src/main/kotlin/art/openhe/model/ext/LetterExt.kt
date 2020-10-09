package art.openhe.model.ext

import art.openhe.model.Letter
import art.openhe.model.User
import art.openhe.util.DbUpdate
import org.apache.commons.lang3.StringUtils


fun Letter.isReply() = StringUtils.isNotBlank(this.parentId)

fun Letter.updateWith(vararg fieldAndValues: Pair<String, Any>?): DbUpdate = DbUpdate(id, *fieldAndValues)