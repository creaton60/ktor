package io.ktor.network.sockets

import io.ktor.network.selector.*
import io.ktor.network.util.*
import java.nio.channels.*

/**
 * TCP socket builder
 */
@Suppress("PublicApiImplicitType")
actual class TCPSocketBuilder(
    private val selector: SelectorManager,
    override var options: SocketOptions
) : Configurable<TCPSocketBuilder, SocketOptions> {
    /**
     * Connect to [hostname] and [port].
     */
    actual suspend fun connect(
        hostname: String,
        port: Int,
        configure: SocketOptions.TCPClientSocketOptions.() -> Unit
    ): Socket {
        val options = options.peer().tcp()
        configure(options)
        assignOptions(options)
        nonBlocking()

        SocketImpl(this, socket()!!, selector).apply {
            connect(remoteAddress)
        }
    }

    /**
     * Bind server socket at [port] to listen to [hostname].
     */
    actual fun bind(
        hostname: String,
        port: Int,
        configure: SocketOptions.AcceptorOptions.() -> Unit
    ): ServerSocket = selector.buildOrClose({ openDatagramChannel() }) {
        val options = options.udp()
        configure(options)
        assignOptions(options)
        nonBlocking()

        DatagramSocketImpl(this, selector).apply {
            channel.socket().bind(localAddress)
            channel.connect(remoteAddress)
        }
    }

    /**
     * Connect to [remoteAddress].
     */
    actual suspend fun connect(
        remoteAddress: NetworkAddress,
        configure: SocketOptions.TCPClientSocketOptions.() -> Unit
    ): Socket {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Bind server socket to listen to [localAddress].
     */
    actual fun bind(
        localAddress: NetworkAddress?,
        configure: SocketOptions.AcceptorOptions.() -> Unit
    ): ServerSocket {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun SelectableChannel.nonBlocking() {
    configureBlocking(false)
}
