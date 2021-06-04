package art.openhe.util

import art.openhe.model.Avatar
import org.joda.time.DateTimeUtils
import kotlin.random.Random


object RandomUtil {

    private val random: Random = Random(DateTimeUtils.currentTimeMillis())

    fun randomAvatar(): Avatar =
        Avatar.values()[random.nextInt(Avatar.values().size)]
}