package art.openhe

import art.openhe.brains.Mailman
import art.openhe.brains.Notifier
import art.openhe.brains.RecipientFinder
import art.openhe.config.EnvConfig
import art.openhe.storage.dao.LetterDao
import art.openhe.storage.dao.UserDao
import art.openhe.model.Letter
import art.openhe.model.LetterCategory
import art.openhe.model.User
import art.openhe.util.logInfo
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory


internal open class BaseTest {

    protected val letterDao: LetterDao = mockk(relaxed = true, relaxUnitFun = true)
    protected val userDao: UserDao = mockk(relaxed = true, relaxUnitFun = true)
    protected val recipientFinder: RecipientFinder = mockk(relaxed = true, relaxUnitFun = true)
    protected val notifier: Notifier = mockk(relaxed = true, relaxUnitFun = true)
    protected var mailman: Mailman = mockk(relaxed = true, relaxUnitFun = true)
    protected val envConfig: EnvConfig = mockk(relaxed = true, relaxUnitFun = true)
    protected val letter = letter()
    protected val user = user()

    @BeforeAll
    fun beforeAll() {
        MockKAnnotations.init(this)
        // disable mockk debug-level logging
        (LoggerFactory.getLogger(ROOT_LOGGER_NAME) as Logger).level = Level.INFO
    }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    protected fun letter() =
        Letter(
            id = ObjectId().toHexString(),
            authorId = "authorId",
            authorAvatar = "authorAvatar",
            recipientId = "recipientId",
            recipientAvatar = "recipientAvatar",
            parentId = "parentId",
            childId = "childId",
            parentPreview = "parentPreview",
            childPreview = "childPreview",
            category = LetterCategory.THOUGHT,
            body = "body"
        )

    protected fun user() =
        User(
            id = ObjectId().toHexString(),
            email = "email",
            googleId = "googleId",
            avatar = "avatar"
        )


    protected inner class Given(msg: String, function: () -> Unit) {
        init { logAndInvoke("GIVEN: $msg", function) }
    }

    protected inner class When(msg: String, function: () -> Unit) {
        init { logAndInvoke("WHEN: $msg", function) }
    }

    protected inner class Then(msg: String, function: () -> Unit) {
        init { logAndInvoke("THEN: $msg", function)  }
    }

    /**
     * Helper function which allows the subclass's name to be displayed by the logger
     */
    private fun logAndInvoke(msg: String, function: () -> Unit) {
        logInfo { msg }
        function.invoke()
    }
}