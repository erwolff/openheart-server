package art.openhe.brains

import art.openhe.BaseTest
import art.openhe.model.Letter
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class LetterPreviewerTest : BaseTest() {

    private lateinit var spiedLetterPreviewer: LetterPreviewer

    @BeforeEach
    fun setup() {
        spiedLetterPreviewer = spyk(LetterPreviewer(letterDao))
    }

    @Test
    fun test_lambda_findParentLetter() {
        Given("A parentId") {
            val parentId = "parentId"

            When("The findParentLetter lambda is called") {
                spiedLetterPreviewer.findParentLetter(parentId)

                Then("letterDao#findById should be called with this parentId") {
                    verify(exactly = 1) { letterDao.findById(eq(parentId)) }
                }
            }
        }
    }

    @Test
    fun test_lambda_preview() {
        Given("A letter with a large body") {
            val letter = letter.clone(body = RandomStringUtils.randomAlphanumeric(300))

            When("The findParentLetter lambda is called") {
                val actual = spiedLetterPreviewer.preview(letter)

                Then("letterDao#findById should be called with this parentId") {
                    assertEquals(280, actual.length)
                }
            }
        }
    }

    @Test
    fun test_process_nullParentId() {
        Given("A letter with null parentId") {
            val letter = mockk<Letter>()
            every { letter.parentId } returns null

            When("LetterPreviewer#process is called") {
                spiedLetterPreviewer.process(letter)

                Then("The letter is not processed") {
                    verify(exactly = 0) { letterDao.findById(any()) }
                }
            }
        }
    }

    @Test
    fun test_process_noParentLetterFound() {
        Given("A letter with a non-existing parent") {
            every { letterDao.findById(eq(letter.parentId!!)) } returns null

            When("LetterPreviewer#process is called") {
                spiedLetterPreviewer.process(letter)

                Then("The letter is not processed") {
                    verify(exactly = 0) { letterDao.update(any()) }
                }
            }
        }
    }

    @Test
    fun test_process_updatesParentAndChildPreviews() {
        Given("A letter with a valid parent") {
            val parentLetter = letter().clone(body = "parentBody")
            every { letterDao.findById(eq(letter.parentId!!)) } returns parentLetter

            When("LetterPreviewer#process is called") {
                spiedLetterPreviewer.process(letter)

                Then("The letter and parent are updated with preview text") {
                    verify(exactly = 2) { letterDao.update(any()) }
                }
            }
        }
    }

}