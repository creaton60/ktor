package io.ktor.network.sockets

import io.ktor.network.selector.*
import io.ktor.network.sockets.DatagramSocketImpl
import io.ktor.network.util.*
import java.net.*

/**
 * UDP socket builder
 */
actual class UDPSocketBuilder(
    private val selector: JvmSelectorManager,
    override var options: SocketOptions.UDPSocketOptions
) : Configurable<UDPSocketBuilder, SocketOptions.UDPSocketOptions> {
    /**
     * Bind server socket to listen to [localAddress]
     */
    actual fun bind(
        localAddress: NetworkAddress?,
        configure: SocketOptions.UDPSocketOptions.() -> Unit
    ): BoundDatagramSocket = bind(localAddress?.toSocketAddress(), configure)

    /**
     * Bind server socket to listen to [localAddress]
     */
    fun bind(
        localAddress: SocketAddress?,
        configure: SocketOptions.UDPSocketOptions.() -> Unit = {}
    ): BoundDatagramSocket = selector.buildOrClose({ openDatagramChannel() }) {
        val options = options.udp()
        configure(options)
        assignOptions(options)
        nonBlocking()

        DatagramSocketImpl(this, selector).apply {
            channel.socket().bind(localAddress)
        }
    }

    /**
     * Create a datagram socket to listen datagrams at [localAddress] and set to [remoteAddress]
     */
    actual fun connect(
        remoteAddress: NetworkAddress,
        localAddress: NetworkAddress?,
        configure: SocketOptions.UDPSocketOptions.() -> Unit
    ): ConnectedDatagramSocket = connect(remoteAddress.toSocketAddress(), localAddress?.toSocketAddress(), configure)

    /**
     * Create a datagram socket to listen datagrams at [localAddress] and set to [remoteAddress]
     */
    fun connect(
        remoteAddress: SocketAddress,
        localAddress: SocketAddress?,
        configure: SocketOptions.UDPSocketOptions.() -> Unit = {}
    ): ConnectedDatagramSocket = selector.buildOrClose({ openDatagramChannel() }) {
        val options = options.udp()
        configure(options)
        assignOptions(options)
        nonBlocking()

        DatagramSocketImpl(this, selector).apply {
            channel.socket().bind(localAddress)
            channel.connect(remoteAddress)
        }
    }

}

internal actual fun UDPSocketBuilder(
    selector: SelectorManager,
    options: SocketOptions.UDPSocketOptions
): UDPSocketBuilder {
    require(selector is JvmSelectorManager)
    return UDPSocketBuilder(selector, options)
}
