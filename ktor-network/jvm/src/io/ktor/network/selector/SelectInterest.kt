package io.ktor.network.selector

import io.ktor.util.*
import java.nio.channels.*

/**
 * Select interest kind
 * @property flag to be set in NIO selector
 */
@Suppress("KDocMissingDocumentation")
@KtorExperimentalAPI
actual enum class SelectInterest(actual val flag: Int) {
    READ(SelectionKey.OP_READ),
    WRITE(SelectionKey.OP_WRITE),
    ACCEPT(SelectionKey.OP_ACCEPT),
    CONNECT(SelectionKey.OP_CONNECT);

    actual companion object {
        @InternalAPI
        actual val AllInterests: Array<SelectInterest> = values()

        /**
         * interest's flags in enum entry order
         */
        @InternalAPI
        actual val flags: IntArray = values().map { it.flag }.toIntArray()

        @InternalAPI
        actual val size: Int = values().size
    }
}
