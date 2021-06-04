package art.openhe.util

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


object ProfanityFilter {
    private val replaceableWords: MutableMap<String, String> = mutableMapOf()
    private val filteredWords: MutableSet<String> = mutableSetOf()

    init {
        loadFilteredWords()
        loadReplaceableWords()
    }

    /**
     * Replaces all words that match the profanity filters
     */
    fun filter(input: String): Pair<String, Boolean> {
        var output = input
        val temp = input.toLowerCase()
        var didContainProfanity = false
        replaceableWords.keys.forEach{
            if (temp.contains(it)) {
                // TODO: match case with input
                output = output.replace(Regex(it, RegexOption.IGNORE_CASE),  replaceableWords[it] ?: "[naughty word]")
                didContainProfanity = true
            }
        }
        filteredWords.forEach {
            if (temp.contains(it)) {
                output = output.replace(Regex(it, RegexOption.IGNORE_CASE),  "[naughty word]")
                didContainProfanity = true
            }
        }

        return output to didContainProfanity
    }

    private fun getMatchingCaseReplacement(word: String, replacement: String) {
        TODO()
    }

    private fun loadFilteredWords() {
        try {
            val reader = BufferedReader(
                InputStreamReader(
                    ClassLoader.getSystemResource("profanity_filter.txt")
                        .openConnection().getInputStream()
                )
            )

            var line = reader.readLine()
            while (line != null) {
                val content = line.split(",")
                if (content.isEmpty()) {
                    continue
                }
                filteredWords.add(content[0])
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadReplaceableWords() {
        try {
            val reader = BufferedReader(
                InputStreamReader(
                    ClassLoader.getSystemResource("replaceable_profanity.txt")
                        .openConnection().getInputStream()
                )
            )

            var line = reader.readLine()
            while (line != null) {
                val content = line.split(",")
                if (content.isEmpty()) {
                    continue
                }
                val wordAndReplacement = content[0].split(":")
                if (wordAndReplacement.isEmpty()) {
                    continue
                }
                replaceableWords[wordAndReplacement[0]] = wordAndReplacement[1]
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}