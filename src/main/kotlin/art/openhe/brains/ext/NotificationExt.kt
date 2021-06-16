package art.openhe.brains.ext

import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification


/**
 * Converts a Notification into a Firebase Message for sending to user devices
 */
fun Notification.toMessage(letterId: String, recipientId: String): Message =
    Message.builder().setNotification(this)
        .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
        .putData("letterId", letterId)
        .setTopic(recipientId)
        .build()