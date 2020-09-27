package art.openhe

import art.openhe.brains.Notifier
import art.openhe.brains.RecipientFinder
import art.openhe.config.EnvConfig
import art.openhe.dao.LetterDao
import art.openhe.dao.UserDao
import art.openhe.util.logger
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach


internal open class BaseTest {

    private val log = logger()

    protected val letterDao: LetterDao = mockk()
    protected val userDao: UserDao = mockk()
    protected val recipientFinder: RecipientFinder = mockk()
    protected val notifier: Notifier = mockk()
    protected val envConfig: EnvConfig = mockk()

    @BeforeAll
    fun beforeAll() {
        MockKAnnotations.init(this)
    }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    protected inner class Given(msg: String, function: () -> Unit) {
        init {
            log.info("Given: $msg")
            function.invoke()
        }
    }

    protected inner class When(msg: String, function: () -> Unit) {
        init {
            log.info("When: $msg")
            function.invoke()
        }
    }

    protected inner class Then(msg: String, function: () -> Unit) {
        init {
            log.info("Then: $msg")
            function.invoke()
        }

    }
}