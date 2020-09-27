package art.openhe.brains

import art.openhe.BaseTest
import art.openhe.model.Letter
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MailmanTest : BaseTest() {

    @InjectMockKs
    lateinit var mailman: Mailman

    @Test
    fun test_mail_noRecipientFound_notifierNeverCalled() {
        Given("A letter with no parentId and no recipient is found") {
            val mailman = spyk(Mailman(letterDao, userDao, recipientFinder, notifier, envConfig))
            val letter: Letter = mockk()
            every { letter.parentId } returns ""
            every { letter.authorId } returns "authorId"
            every { mailman.findRecipient(any()) } returns null

            When("Mailman receives this letter") {
                mailman.mail(letter)

                Then("Notifier should not notify a recipient") {
                    verify(exactly = 0) { notifier.receivedReply(any(), any(), any()) }
                    verify(exactly = 0) { notifier.receivedLetter(any(), any(), any()) }
                }
            }
        }
    }

    @Test
    fun test_mail_noRecipientAvatar_notifierNeverCalled() {
        Given("A letter with no recipientAvatar") {
            val letter: Letter = mockk()
            every { letter.parentId } returns "parentId"
            every { letter.recipientId } returns "recipientId"
            every { letter.recipientAvatar } returns null
            every { letter.authorId } returns "authorId"

            When("Mailman receives this letter") {
                mailman.mail(letter)

                Then("Notifier should not notify a recipient") {
                    verify(exactly = 0) { mailman.findRecipient(any()) }
                    verify(exactly = 0) { notifier.receivedReply(any(), any(), any()) }
                    verify(exactly = 0) { notifier.receivedLetter(any(), any(), any()) }
                }
            }
        }
    }
}
