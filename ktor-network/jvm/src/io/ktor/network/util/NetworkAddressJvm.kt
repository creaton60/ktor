package io.ktor.network.util

import java.net.*

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual interface NetworkAddress

fun NetworkAddress.toSocketAddress(): SocketAddress {
    TODO()
}

fun SocketAddress.toNetworkAddress(): NetworkAddress {
    TODO()
}
