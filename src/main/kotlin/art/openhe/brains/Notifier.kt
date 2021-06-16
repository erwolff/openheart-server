package art.openhe.brains

import art.openhe.brains.ext.toMessage
import art.openhe.util.logInfo
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Notification
import org.apache.commons.lang3.StringUtils
import javax.inject.Singleton

/**
 * Sends push notifications to user devices
 */
@Singleton
class Notifier {

    /**
     * Notifies a user that he/she has received a new letter
     */
    fun receivedLetter(letterId: String, recipientId: String, recipientAvatar: String) =
        send(receivedLetterNotification(recipientAvatar), letterId, recipientId)


    /**
     * Notifies a user that he/she has received a new reply
     */
    fun receivedReply(letterId: String, recipientId: String, recipientAvatar: String) =
        send(receivedReplyNotification(recipientAvatar), letterId, recipientId)


    /**
     * Notifies a user that he/she has received a heart
     */
    fun receivedHeart(letterId: String, senderAvatar: String?, recipientId: String, recipientAvatar: String) =
        send(receivedHeartNotification(senderAvatar, recipientAvatar), letterId, recipientId)


    /**
     * Sends a welcome letter notification to a user
     */
    fun welcomeLetter(recipientId: String, recipientAvatar: String, welcomeLetterId: String) =
        send(welcomeLetterNotification(recipientAvatar), welcomeLetterId, recipientId)


    private fun send(notification: Notification, letterId: String, recipientId: String) {
        val messageId = notification.toMessage(letterId, recipientId).let {
            FirebaseMessaging.getInstance().send(it)
        }

        logInfo { "Sent notification to recipient $recipientId - messageId: $messageId" }
    }



    // Helper Functions
    
    val receivedLetterNotification = { recipientAvatar: String ->
        Notification.builder()
            .setTitle(title(recipientAvatar))
            .setBody(receivedLetterTxt)
            .build()
    }

    val receivedReplyNotification = { recipientAvatar: String ->
        Notification.builder()
            .setTitle(title(recipientAvatar))
            .setBody(receivedReplyTxt)
            .build()
    }

    val receivedHeartNotification = { senderAvatar: String?, recipientAvatar: String ->
        Notification.builder()
            .setTitle(title(recipientAvatar))
            .setBody(receivedHeartBody(senderAvatar))
            .build()
    }

    val welcomeLetterNotification = { recipientAvatar: String ->
        Notification.builder()
            .setTitle(title(recipientAvatar))
            .setBody(letterFromDev)
            .build()
    }

    val title = { recipientAvatar: String -> StringUtils.replace(titleTxt, "#", recipientAvatar) }
    val receivedHeartBody = { senderAvatar: String? -> senderAvatar?.let { StringUtils.replace(receivedHeartTxt, "#", it) } ?: receivedHeartNoSenderTxt }

    companion object {
        private const val titleTxt = "Dear #"
        private const val receivedLetterTxt = "A letter has found its way to you!"
        private const val receivedReplyTxt = "You've received a reply to your letter!"
        private const val receivedHeartTxt = "# has sent you a heart!"
        private const val receivedHeartNoSenderTxt = "Your reply has received a heart!"
        private const val letterFromDev = "You've received a letter from the developer!"
    }
}