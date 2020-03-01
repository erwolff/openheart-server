package art.openhe.util.ext

import com.mongodb.WriteResult


fun WriteResult.positiveCountOrNull(): Int? =
     if (n > 0) n
     else null
