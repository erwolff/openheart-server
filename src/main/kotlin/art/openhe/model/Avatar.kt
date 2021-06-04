package art.openhe.model


enum class Avatar {
    // intentionally breaking casing conventions for human readable format
    Antelope,
    Bear,
    Crab,
    Dragon,
    Elephant,
    Fox,
    Giraffe,
    Horse,
    Iguana,
    Jellyfish,
    Koala,
    Lion,
    Monkey,
    Narwhal,
    Owl,
    Pig,
    Quail,
    Rabbit,
    Sparrow,
    Tiger,
    Unicorn,
    Vulture,
    Whale,
    Xylophone,
    Yak,
    Zebra
}

fun Avatar.fromString(value: String): Avatar? =
    Avatar.values().firstOrNull {
        it.name.equals(value, ignoreCase = true)
    }
