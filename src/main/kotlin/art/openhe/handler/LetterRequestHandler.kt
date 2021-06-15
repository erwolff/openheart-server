package art.openhe.handler

import art.openhe.brains.LetterSanitizer
import art.openhe.brains.Notifier
import art.openhe.storage.dao.LetterDao
import art.openhe.storage.dao.UserDao
import art.openhe.storage.dao.criteria.Pageable
import art.openhe.storage.dao.criteria.Sort
import art.openhe.storage.dao.criteria.ValueCriteria.Companion.isFalse
import art.openhe.storage.dao.criteria.ValueCriteria.Companion.isNotNull
import art.openhe.storage.dao.criteria.ValueCriteria.Companion.isNull
import art.openhe.storage.dao.ext.find
import art.openhe.model.Letter
import art.openhe.model.request.LetterRequest
import art.openhe.model.response.*
import art.openhe.queue.Queues
import art.openhe.queue.producer.SqsMessageProducer
import art.openhe.util.DbUpdate
import art.openhe.util.ext.eqCriteria
import art.openhe.validator.LetterRequestValidator
import org.joda.time.DateTimeUtils
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

/**
 * Handles all LetterRequest-related operations
 * Interacts with the Validator and Storage layers
 * Returns HandlerResult objects to indicate success/failure
 */
@Singleton
class LetterRequestHandler
@Inject constructor(
    private val validator: LetterRequestValidator,
    private val letterDao: LetterDao,
    private val userDao: UserDao,
    private val letterSanitizer: LetterSanitizer,
    private val producer: SqsMessageProducer,
    private val notifier: Notifier
) : Handler {

    /**
     * Validates, sanitizes, saves, and processes a new letter
     *
     * Success: Returns an EmptyResponse
     * Failure: Returns a LetterErrorResponse with status BAD_REQUEST
     */
    fun writeLetter(
        request: LetterRequest,
        authorId: String
    ): HandlerResult<EmptyResponse, LetterErrorResponse> =
        with (request) {
            // validate
            validateCreate(this)?.let { return it.asFailure() }

            // process
            toLetter(authorId)
                .let(sanitizeLetter)
                .let(saveLetter)
                ?.apply(processSavedLetter)

            return emptyResponse
        }


    /**
     * Retrieves the letter with the supplied id
     *
     * Success: Returns the Letter as a LetterResponse
     * Failure: Returns a LetterErrorResponse with status NOT_FOUND
     */
    fun getLetter(
        id: String
    ): HandlerResult<LetterResponse, LetterErrorResponse> =
         findLetter(id)?.toLetterResponse()?.asSuccess()
             ?: letterNotFoundError(id)


    /**
     * Retrieves all sent letters (paginated) for the user with this
     * authorId sorted by 'writtenTimestamp' descending
     *
     * Success: Returns a PageResponse containing all matching Letters
     * Failure: Returns a PageErrorResponse with status BAD_REQUEST
     */
    fun getSentLetters(
        authorId: String,
        page: Int,
        size: Int,
        hearted: Boolean? = null,
        reply: Boolean? = null
    ): HandlerPageResult<LetterResponse, PageErrorResponse> =
        if (page < 1)
            invalidPageError

        else
            letterDao.find(
                pageable = Pageable(page, size, Sort.Letters.byWrittenTimestampDesc()),
                authorId = authorId.eqCriteria(),
                hearted = hearted?.eqCriteria(),
                parentId = if (reply == true) isNotNull() else isNull()
            ).asPageResponse { it.toLetterResponse() }.asSuccess()


    /**
     * Retrieves all received letters (paginated) for the user with this
     * recipientId sorted by 'sentTimestamp' descending
     *
     * Success: Returns a PageResponse containing all matching Letters
     * Failure: Returns a PageErrorResponse with status BAD_REQUEST
     */
    fun getReceivedLetters(
        recipientId: String,
        page: Int,
        size: Int
    ): HandlerPageResult<LetterResponse, PageErrorResponse> =
        if (page < 1)
            invalidPageError

        else
            letterDao.find(
                pageable = Pageable(page, size, Sort.Letters.bySentTimestampDesc()),
                recipientId = recipientId.eqCriteria(),
                deleted = isFalse()
            ).asPageResponse { it.toLetterResponse() }.asSuccess()


    /**
     * Sets 'hearted' to 'true' for the letter with the supplied id
     *
     * Success: Returns an EmptyResponse
     * Failure: Returns a LetterErrorResponse with status NOT_FOUND
     */
    fun heartLetter(id: String): HandlerResult<EmptyResponse, LetterErrorResponse> =
        updateLetter(setHeartedToTrue(id))
            ?.apply(notifyReceivedHeart)?.toEmptyResponse()?.asSuccess()
            ?: letterNotFoundError(id)


    /**
     * Sets 'flagged' and 'deleted' to 'true' for the letter with the supplied id
     *
     * Success: Returns an EmptyResponse
     * Failure: Returns a LetterErrorResponse with status NOT_FOUND
     */
    fun reportLetter(id: String): HandlerResult<EmptyResponse, LetterErrorResponse> =
        updateLetter(setFlaggedAndDeletedToTrue(id))?.toEmptyResponse()?.asSuccess()
            ?: letterNotFoundError(id)


    /**
     * Sets 'readTimestamp' to 'now' for the letter with the supplied id
     *
     * Success: Returns an EmptyResponse
     * Failure: Returns a LetterErrorResponse with status NOT_FOUND
     */
    fun markAsRead(id: String): HandlerResult<EmptyResponse, LetterErrorResponse> =
        updateLetter(setReadTimestampToNow(id))?.toEmptyResponse()?.asSuccess()
            ?: letterNotFoundError(id)


    /**
     * Sets 'deleted' to 'true' for the letter with the supplied id
     *
     * Success: Returns an EmptyResponse
     * Failure: Returns a LetterErrorResponse with status NOT_FOUND
     */
    fun deleteLetter(id: String): HandlerResult<EmptyResponse, LetterErrorResponse> =
        updateLetter(setDeletedToTrue(id))?.toEmptyResponse()?.asSuccess()
            ?: letterNotFoundError(id)


    // Helper Functions

    private val validateCreate = { request: LetterRequest -> validator.validateCreate(request) }
    private val sanitizeLetter = { letter: Letter -> letterSanitizer.sanitize(letter) }
    private val saveLetter = { letter: Letter -> letterDao.save(letter) }
    private val updateLetter = { dbUpdate: DbUpdate -> letterDao.update(dbUpdate) }
    private val findLetter = { id: String -> letterDao.findById(id) }
    private val updateUser = { dbUpdate: DbUpdate -> userDao.update(dbUpdate) }
    private val processSavedLetter = { letter: Letter -> processSavedLetter(letter) }
    private val notifyReceivedHeart = { letter: Letter -> with (letter) { notifier.receivedHeart(id, recipientAvatar, authorId, authorAvatar) } }
    private val publishToMailman = { letter: Letter -> producer.publish(letter, Queues.mailman) }
    private val publishToPreviewer = { letter: Letter -> producer.publish(letter, Queues.letterPreviewer) }

    /**
     * Dispatches the letter to the Mailman,
     * Dispatches the letter to the LetterPreviewer if parentId is non-null
     * Updates the author's 'lastSentLetterTimestamp'
     */
    private fun processSavedLetter(
        letter: Letter
    ) {
        with (letter) {
            publishToMailman(this)
            parentId?.let { publishToPreviewer(this) }
            updateUser(DbUpdate(authorId, "lastSentLetterTimestamp" to DateTimeUtils.currentTimeMillis()))
        }
    }


    companion object {
        private val invalidPageError = PageErrorResponse(Response.Status.BAD_REQUEST, page = "Supplied page must be greater than 0").asPageFailure()
        private val letterNotFoundError = { id: String -> LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist").asFailure() }
        private val setHeartedToTrue = { id: String -> DbUpdate(id, "hearted" to true) }
        private val setDeletedToTrue = { id: String -> DbUpdate(id, "deleted" to true) }
        private val setReadTimestampToNow = { id: String -> DbUpdate(id, "readTimestamp" to DateTimeUtils.currentTimeMillis()) }
        private val setFlaggedAndDeletedToTrue = { id: String -> DbUpdate(id, "flagged" to true, "deleted" to true) }
    }


}