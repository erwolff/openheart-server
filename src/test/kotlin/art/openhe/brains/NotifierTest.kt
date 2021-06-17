package art.openhe.brains

import art.openhe.BaseTest
import io.mockk.spyk
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NotifierTest : BaseTest() {

    @Test
    fun test_lambda_title() {
        Given("A recipientAvatar") {
            val notifier = spy()
            val recipientAvatar = "recipientAvatar"
            When("The title lambda is called") {
                val actual = notifier.title(recipientAvatar)

                Then("The correct text should be returned") {
                    assertEquals(StringUtils.replace(Notifier.titleTxt, "#", recipientAvatar), actual)
                }
            }
        }
    }

    @Test
    fun test_lambda_receivedHeartBody_withSenderAvatar() {
        Given("A non-null senderAvatar") {
            val notifier = spy()
            val senderAvatar = "senderAvatar"
            When("The receivedHeartBody lambda is called") {
                val actual = notifier.receivedHeartBody(senderAvatar)

                Then("The correct text should be returned") {
                    assertEquals(StringUtils.replace(Notifier.receivedHeartTxt, "#", senderAvatar), actual)
                }
            }
        }
    }

    @Test
    fun test_lambda_receivedHeartBody_nullSenderAvatar() {
        Given("A null senderAvatar") {
            val notifier = spy()
            val senderAvatar = null
            When("The receivedHeartBody lambda is called") {
                val actual = notifier.receivedHeartBody(senderAvatar)

                Then("The correct text should be returned") {
                    assertEquals(Notifier.receivedHeartNoSenderTxt, actual)
                }
            }
        }
    }


    private fun spy(): Notifier =
        spyk(Notifier())


}