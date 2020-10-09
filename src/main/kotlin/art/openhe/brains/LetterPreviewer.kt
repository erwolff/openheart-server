package art.openhe.brains

import art.openhe.dao.LetterDao
import art.openhe.model.Letter
import art.openhe.model.ext.updateWith
import art.openhe.util.logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LetterPreviewer
@Inject constructor(private val letterDao: LetterDao) {

    private val log = logger()

    fun process(letter: Letter) {
        if (letter.parentId == null) {
            return
        }

        val parent = letterDao.findById(letter.parentId) ?: return

        val parentPreview = parent.body.substring(0, minOf(parent.body.length, 280))
        val childPreview = letter.body.substring(0, minOf(letter.body.length, 280))

        log.info("Updating parentPreview of letter ${letter.id}")
        letterDao.update(letter.updateWith("parentPreview" to parentPreview))

        log.info("Updating childPreview of letter ${parent.id}")
        letterDao.update(parent.updateWith("childPreview" to childPreview))
    }
}