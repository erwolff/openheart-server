package art.openhe.storage.dao.criteria


sealed class Sort {

    private companion object {
        private const val asc: String = "1"
        private const val desc: String = "-1"
    }

    object Letters {
        fun byCreatedTimestampAsc(): Sort = CreatedTimestamp(asc)
        fun byCreatedTimestampDesc(): Sort = CreatedTimestamp(desc)
        fun bySentTimestampAsc(): Sort = SentTimestamp(asc)
        fun bySentTimestampDesc(): Sort = SentTimestamp(desc)
        fun byWrittenTimestampAsc(): Sort = WrittenTimestamp(asc)
        fun byWrittenTimestampDesc(): Sort = WrittenTimestamp(desc)
    }

    object Users {
        fun byCreatedTimestampAsc(): Sort = CreatedTimestamp(asc)
        fun byCreatedTimestampDesc(): Sort = CreatedTimestamp(desc)
        fun byLastSentLetterTimestampAsc(): Sort = LastSentLetterTimestamp(asc)
        fun byLastSentLetterTimestampDesc(): Sort = LastSentLetterTimestamp(desc)
        fun byLastReceivedLetterTimestampAsc(): Sort = LastReceivedLetterTimestamp(asc)
        fun byLastReceivedLetterTimestampDesc(): Sort = LastReceivedLetterTimestamp(desc)
    }

    private data class CreatedTimestamp(val value: String): Sort()
    private data class SentTimestamp(val value: String): Sort()
    private data class WrittenTimestamp(val value: String): Sort()
    private data class LastSentLetterTimestamp(val value: String): Sort()
    private data class LastReceivedLetterTimestamp(val value: String): Sort()

    fun toSort(): String = when(this) {
        is CreatedTimestamp -> "{ createdTimestamp: ${this.value} }"
        is SentTimestamp -> "{ sentTimestamp: ${this.value} }"
        is WrittenTimestamp -> "{ writtenTimestamp: ${this.value} }"
        is LastSentLetterTimestamp -> "{ lastSentLetterTimestamp: ${this.value} }"
        is LastReceivedLetterTimestamp -> "{ lastReceivedLetterTimestamp: ${this.value} }"
    }
}