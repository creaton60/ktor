package io.ktor.network.selector

import io.ktor.util.*
import java.nio.channels.*

@InternalAPI
interface JvmSelectable : Selectable {
    val channel: SelectableChannel
}
