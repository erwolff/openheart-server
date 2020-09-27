package art.openhe.model.ext

import art.openhe.model.Letter
import org.apache.commons.lang3.StringUtils


fun Letter.isReply() = StringUtils.isNotBlank(this.parentId)