package art.openhe.model.ext

import art.openhe.model.User
import art.openhe.util.DbUpdate


fun User.updateWith(vararg fieldAndValues: Pair<String, Any>?): DbUpdate = DbUpdate(id, *fieldAndValues)