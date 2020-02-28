package art.openhe.util

import org.mindrot.jbcrypt.BCrypt


class AuthUtil {

    companion object {

        fun saltAndHash(input: String): String =
            BCrypt.hashpw(input, BCrypt.gensalt())

        fun compare(plainText: String, saltedAndHashed: String):Boolean =
            BCrypt.checkpw(plainText, saltedAndHashed)

    }
}