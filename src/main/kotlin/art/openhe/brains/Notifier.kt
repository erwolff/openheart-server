package art.openhe.brains

import art.openhe.config.EnvConfig
import art.openhe.util.logInfo
import art.openhe.util.logger
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.apache.commons.lang3.StringUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Notifier
@Inject constructor(private val envConfig: EnvConfig) {

    private val titleTxt = "Dear #"
    private val receivedLetterTxt = "A letter has found its way to you!"
    private val receivedReplyTxt = "You've received a reply to your letter!"
    private val receivedHeartTxt = "# has sent you a heart!"
    private val receivedHeartNoSenderTxt = "Your reply has received a heart!"
    private val letterFromDev = "You've received a letter from the developer!"

    fun receivedLetter(letterId: String, recipientId: String, recipientAvatar: String) {
        val notification = Notification.builder()
            .setTitle(title(recipientAvatar))
            .setBody(receivedLetterTxt)
            .build()

        send(notification, letterId, recipientId)
    }

    fun receivedReply(letterId: String, recipientId: String, recipientAvatar: String) {
        val notification = Notification.builder()
            .setTitle(title(recipientAvatar))
            .setBody(receivedReplyTxt)
            .build()

        send(notification, letterId, recipientId)
    }

    fun receivedHeart(letterId: String, senderAvatar: String?, recipientId: String, recipientAvatar: String) {
        val notification = Notification.builder()
            .setTitle(title(recipientAvatar))
            .setBody(senderAvatar?.let { receivedHeartBody(it) } ?: receivedHeartNoSenderTxt)
            .build()

        send(notification, letterId, recipientId)
    }

    fun welcomeLetter(recipientId: String, recipientAvatar: String, welcomeLetterId: String) {
        val notification = Notification.builder()
            .setTitle(title(recipientAvatar))
            .setBody(letterFromDev)
            .build()

        send(notification, welcomeLetterId, recipientId)
    }

    private fun send(notification: Notification, letterId: String, recipientId: String) {
        val message = Message.builder().setNotification(notification)
            .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
            .putData("letterId", letterId)
            .setTopic(recipientId)
            .build()

        val messageId = FirebaseMessaging.getInstance().send(message)

        logInfo { "Sent notification to recipient $recipientId - messageId: $messageId" }
    }

    // Helper Functions
    
    val title = { recipientAvatar: String -> StringUtils.replace(titleTxt, "#", recipientAvatar) }
    val receivedHeartBody = { senderAvatar: String -> StringUtils.replace(receivedHeartTxt, "#", senderAvatar) }
}