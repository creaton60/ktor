package io.ktor.network.sockets

import io.ktor.network.selector.*
import io.ktor.network.util.*
import java.net.*
import java.nio.channels.*

internal class SocketImpl<out S : SocketChannel>(
    override val channel: S,
    private val socket: java.net.Socket,
    selector: SelectorManager
) : NIOSocketImpl<S>(channel, selector, pool = null), Socket {
    init {
        require(!channel.isBlocking) { "channel need to be configured as non-blocking" }
    }

    override val localAddress: NetworkAddress
        get() = socket.localSocketAddress.toNetworkAddress()

    override val remoteAddress: NetworkAddress
        get() = socket.remoteSocketAddress.toNetworkAddress()

    @Suppress("BlockingMethodInNonBlockingContext")
    internal suspend fun connect(target: SocketAddress): Socket {
        if (channel.connect(target)) return this

        wantConnect(true)
        selector.select(this, SelectInterest.CONNECT)

        while (true) {
            if (channel.finishConnect()) break

            wantConnect(true)
            selector.select(this, SelectInterest.CONNECT)
        }

        wantConnect(false)

        return this
    }

    private fun wantConnect(state: Boolean = true) {
        interestOp(SelectInterest.CONNECT, state)
    }
}
