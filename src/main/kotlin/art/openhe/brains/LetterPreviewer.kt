package art.openhe.brains

import art.openhe.storage.dao.LetterDao
import art.openhe.model.Letter
import art.openhe.model.ext.updateWith
import art.openhe.util.logInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LetterPreviewer
@Inject constructor(
    private val letterDao: LetterDao
) {

    fun process(letter: Letter) {
        letter.parentId?.let(findParentLetter)?.let { parent ->
            logInfo { "Updating parentPreview of letter ${letter.id}" }
            letterDao.update(letter.updateWith("parentPreview" to preview(parent)))

            logInfo { "Updating childPreview of letter ${parent.id}" }
            letterDao.update(parent.updateWith("childPreview" to preview(letter)))
        }
    }

    // Helper Functions

    private val findParentLetter = { parentId: String -> letterDao.findById(parentId) }

    companion object {
        private val preview = { letter: Letter -> letter.body.substring(0, minOf(letter.body.length, 280)) }
    }
}
