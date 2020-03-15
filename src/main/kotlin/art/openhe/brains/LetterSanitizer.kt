package art.openhe.brains

import art.openhe.model.Letter
import art.openhe.util.ProfanityFilter
import javax.inject.Singleton

@Singleton
class LetterSanitizer {

    // TODO: This is just a baseline sanitization
    fun sanitize(letter: Letter): Letter {
        val result = ProfanityFilter.filter(letter.body)
        return letter.update(body = result.first, flagged = result.second)
    }


}