package art.openhe

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
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory


internal open class BaseTest {

    protected val letterDao: LetterDao = mockk(relaxed = true, relaxUnitFun = true)
    protected val userDao: UserDao = mockk(relaxed = true, relaxUnitFun = true)
    protected val recipientFinder: RecipientFinder = mockk(relaxed = true, relaxUnitFun = true)
    protected val notifier: Notifier = mockk(relaxed = true, relaxUnitFun = true)
    protected val envConfig: EnvConfig = mockk(relaxed = true, relaxUnitFun = true)
    protected val letter: Letter = mockk()
    protected val user: User = mockk()

    @BeforeAll
    fun beforeAll() {
        MockKAnnotations.init(this)
        // disable mockk debug-level logging
        (LoggerFactory.getLogger(ROOT_LOGGER_NAME) as Logger).level = Level.INFO
    }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        resetLetterMock()
        resetUserMock()
    }

    private fun resetLetterMock() {
        every { letter.id } returns "id"
        every { letter.authorId } returns "authorId"
        every { letter.authorAvatar } returns "authorAvatar"
        every { letter.recipientId } returns "recipientId"
        every { letter.recipientAvatar } returns "recipientAvatar"
        every { letter.parentId } returns "parentId"
        every { letter.childId } returns "childId"
        every { letter.parentPreview } returns "parentPreview"
        every { letter.childPreview } returns "childPreview"
        every { letter.category } returns LetterCategory.THOUGHT
        every { letter.body } returns "body"
    }

    private fun resetUserMock() {
        every { user.id } returns "id"
        every { user.email } returns "email"
        every { user.googleId } returns "googleId"
        every { user.avatar } returns "avatar"
    }

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