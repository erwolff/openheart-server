package art.openhe.brains

import art.openhe.BaseTest
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MailmanTest : BaseTest() {

    @InjectMockKs
    lateinit var mailman: Mailman

    @Test
    fun test_mail_withParentId_processesAsReply() {
        Given("A letter with valid parentId") {
            val mailman = spy()
            every { letter.parentId } returns "parentId"
            every { mailman.processReply(any()) } returns Unit // do nothing
            every { mailman.isFirstLetterByAuthor(any()) } returns false

            When("Mailman receives this letter") {
                mailman.mail(letter)

                Then("Mailman should process as a reply") {
                    verify(exactly = 1) { mailman.processReply(any()) }
                    verify(exactly = 0) { mailman.processNewLetter(any()) }
                    verify(exactly = 0) { mailman.sendWelcomeLetter(any(), any()) }
                }
            }
        }
    }

    @Test
    fun test_mail_noParentId_processesAsNewLetter() {
        Given("A letter without a parentId") {
            val mailman = spy()
            every { letter.parentId } returns ""
            every { mailman.processNewLetter(any()) } returns Unit // do nothing
            every { mailman.isFirstLetterByAuthor(any()) } returns false

            When("Mailman receives this letter") {
                mailman.mail(letter)

                Then("Mailman should process as a new letter") {
                    verify(exactly = 1) { mailman.processNewLetter(any()) }
                    verify(exactly = 0) { mailman.processReply(any()) }
                    verify(exactly = 0) { mailman.sendWelcomeLetter(any(), any()) }
                }
            }
        }
    }

    @Test
    fun test_mail_firstLetterByAuthor_sendsWelcomeLetter() {
        Given("A letter written by a first-time author") {
            val mailman = spy()
            every { mailman.processReply(any()) } returns Unit // do nothing
            every { mailman.isFirstLetterByAuthor(any()) } returns true
            every { mailman.sendWelcomeLetter(any(), any()) } returns Unit // do nothing

            When("Mailman receives this letter") {
                mailman.mail(letter)

                Then("Mailman should send a welcome letter") {
                    verify(exactly = 1) { mailman.sendWelcomeLetter(any(), any()) }
                }
            }
        }
    }

    @Test
    fun test_mail_notFirstLetterByAuthor_doesNotSendWelcomeLetter() {
        Given("A letter written by an author who has written previous letters") {
            val mailman = spy()
            every { mailman.processReply(any()) } returns Unit // do nothing
            every { mailman.isFirstLetterByAuthor(any()) } returns false

            When("Mailman receives this letter") {
                mailman.mail(letter)

                Then("Mailman should not send a welcome letter") {
                    verify(exactly = 0) { mailman.sendWelcomeLetter(any(), any()) }
                }
            }
        }
    }

    @Test
    fun test_findRecipient_noneFound_returnsNull() {
        Given("A letter to find a recipient for") {
            every { recipientFinder.find(any()) } returns null

            When("RecipientFinder is unable to find a recipient") {
                val actual = mailman.findRecipient(letter)

                Then("Null should be returned") {
                    assertNull(actual)
                }
            }
        }
    }

    @Test
    fun test_findRecipient_returnsFoundRecipient() {
        Given("A letter to find a recipient for") {
            every { recipientFinder.find(any()) } returns user

            When("RecipientFinder finds a recipient") {
                val actual = mailman.findRecipient(letter)

                Then("The found user id and avatar should be returned") {
                    assertNotNull(actual)
                    assertEquals(actual!!.first, user.id)
                    assertEquals(actual.second, user.avatar)
                }
            }
        }
    }

    @Test
    fun test_processReply_noRecipientId() {
        Given("A letter to process as reply with no recipientId") {
            val mailman = spy()
            every { letter.recipientId } returns null
            every { mailman.updateOriginalLetter(any(), any()) } returns null
            every { mailman.updateLetter(any(), any(), any()) } returns null
            every { notifier.receivedReply(any(), any(), any()) } returns Unit // do nothing

            When("processReply is called with this letter") {
                mailman.processReply(letter)
                
                Then("The letter should not be processed") {
                    verify(exactly = 0) { mailman.updateOriginalLetter(any(), any()) }
                }
            }
        }
    }

    private fun spy(): Mailman =
        spyk(Mailman(letterDao, userDao, recipientFinder, notifier, envConfig))
}
