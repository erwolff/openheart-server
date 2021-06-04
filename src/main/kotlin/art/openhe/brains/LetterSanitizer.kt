package art.openhe.brains

import art.openhe.model.Letter
import art.openhe.util.ProfanityFilter
import javax.inject.Singleton

@Singleton
class LetterSanitizer {

    // TODO: This is just a baseline sanitization
    fun sanitize(letter: Letter): Letter =
        ProfanityFilter.filter(letter.body).let {
            letter.clone(body = it.first, flagged = it.second)
        }

}