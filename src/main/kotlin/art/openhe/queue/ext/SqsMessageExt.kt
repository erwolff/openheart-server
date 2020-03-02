package art.openhe.queue.ext

import art.openhe.util.MessageCache
import javax.jms.Message


fun <T> Message.letIfNotDuplicate(lambda: (Message) -> T): T? =
    if (MessageCache.isDuplicate(this.jmsMessageID)) null
    else lambda(this)