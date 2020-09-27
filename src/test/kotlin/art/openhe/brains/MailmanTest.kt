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
    fun test_mail_withParentId_processesAsReply() {
        val mailman = spy()
        val letter: Letter = mockk()

        Given("A letter with valid parentId") {
            every { letter.parentId } returns "parentId"
            every { letter.authorId } returns "authorId"
            every { mailman.processReply(any()) } returns Unit // do nothing
            every { mailman.isFirstLetterByAuthor(any()) } returns false
        }

        When("Mailman receives this letter") {
            mailman.mail(letter)
        }

        Then("Mailman should process as a reply") {
            verify(exactly = 1) { mailman.processReply(any()) }
            verify(exactly = 0) { mailman.processNewLetter(any()) }
            verify(exactly = 0) { mailman.sendWelcomeLetter(any(), any()) }
        }
    }

    @Test
    fun test_mail_noParentId_processesAsNewLetter() {
        val mailman = spy()
        val letter: Letter = mockk()

        Given("A letter without a parentId") {
            every { letter.parentId } returns ""
            every { letter.authorId } returns "authorId"
            every { mailman.processNewLetter(any()) } returns Unit // do nothing
            every { mailman.isFirstLetterByAuthor(any()) } returns false
        }

        When("Mailman receives this letter") {
            mailman.mail(letter)
        }

        Then("Mailman should process as a reply") {
            verify(exactly = 1) { mailman.processNewLetter(any()) }
            verify(exactly = 0) { mailman.processReply(any()) }
            verify(exactly = 0) { mailman.sendWelcomeLetter(any(), any()) }
        }
    }

    private fun spy(): Mailman =
        spyk(Mailman(letterDao, userDao, recipientFinder, notifier, envConfig))
}
