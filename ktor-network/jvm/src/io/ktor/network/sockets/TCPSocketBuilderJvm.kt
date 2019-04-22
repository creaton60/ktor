package io.ktor.network.sockets

import io.ktor.network.selector.*
import io.ktor.network.util.*
import java.net.*

/**
 * TCP socket builder
 */
@Suppress("PublicApiImplicitType")
actual class TCPSocketBuilder(
    internal val selector: JvmSelectorManager,
    override var options: SocketOptions
) : Configurable<TCPSocketBuilder, SocketOptions> {
    /**
     * Connect to [hostname] and [port].
     */
    actual suspend fun connect(
        hostname: String,
        port: Int,
        configure: SocketOptions.TCPClientSocketOptions.() -> Unit
    ): Socket = connect(InetSocketAddress(hostname, port), configure)

    /**
     * Bind server socket at [port] to listen to [hostname].
     */
    actual fun bind(
        hostname: String,
        port: Int,
        configure: SocketOptions.AcceptorOptions.() -> Unit
    ): ServerSocket = bind(InetSocketAddress(hostname, port), configure)

    /**
     * Connect to [remoteAddress].
     */
    actual suspend fun connect(
        remoteAddress: NetworkAddress,
        configure: SocketOptions.TCPClientSocketOptions.() -> Unit
    ): Socket = connect(remoteAddress.toSocketAddress(), configure)

    /**
     * Connect to [remoteAddress].
     */
    suspend fun connect(
        remoteAddress: SocketAddress,
        configure: SocketOptions.TCPClientSocketOptions.() -> Unit = {}
    ): Socket = selector.buildOrClose({ openSocketChannel() }) {
        val options = options.peer().tcp()
        configure(options)
        assignOptions(options)
        nonBlocking()

        SocketImpl(this, socket()!!, selector).apply {
            connect(remoteAddress)
        }
    }

    /**
     * Bind server socket to listen to [localAddress].
     */
    actual fun bind(
        localAddress: NetworkAddress?,
        configure: SocketOptions.AcceptorOptions.() -> Unit
    ): ServerSocket = bind(localAddress?.toSocketAddress(), configure)

    /**
     * Bind server socket to listen to [localAddress].
     */
    fun bind(
        localAddress: SocketAddress?,
        configure: SocketOptions.AcceptorOptions.() -> Unit = {}
    ): ServerSocket = selector.buildOrClose({ openServerSocketChannel() }) {
        val options = options.acceptor()
        configure(options)
        assignOptions(options)
        nonBlocking()

        ServerSocketImpl(this, selector).apply {
            channel.socket().bind(localAddress)
        }
    }
}


internal actual fun TCPSocketBuilder(
    selector: SelectorManager,
    options: SocketOptions
): TCPSocketBuilder {
    require(selector is JvmSelectorManager)
    return TCPSocketBuilder(selector, options)
}
