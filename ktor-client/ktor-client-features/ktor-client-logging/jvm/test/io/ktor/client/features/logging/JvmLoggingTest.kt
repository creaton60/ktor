/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.features.logging

import io.ktor.client.engine.mock.*
import io.ktor.client.request.forms.*
import io.ktor.client.tests.utils.*
import kotlinx.io.core.*
import kotlin.test.*


class JvmLoggingTest {
    @Test
    fun testLoggingWithForms() = clientTest(MockEngine) {
        val packetLogger = PacketLogger()

        config {
            engine {
                addHandler { request ->
                    val body = request.body.toByteReadPacket().lines().joinToString("\n")
                    val log = packetLogger.buildLog().lines()
                        .drop(4)
                        .dropLast(3)
                        .joinToString("\n")

                    assertEquals(log, body)
                    respondOk()
                }
            }

            install(Logging) {
                level = LogLevel.BODY
                logger = packetLogger
            }
        }

        test { client ->
            client.submitFormWithBinaryData<Unit>(formData = formData {
                append("name", "sunny day")
                append("image", "image.jpg") {
                    writeStringUtf8("image-content")
                }
            })
        }
    }
}

private class PacketLogger : Logger {
    private val packet = BytePacketBuilder()

    override fun log(message: String) {
        packet.writeStringUtf8("$message\n")
    }

    fun buildLog(): ByteReadPacket = packet.build()
}

private fun ByteReadPacket.lines(): List<String> = mutableListOf<String>().apply {
    while (isNotEmpty) {
        this += readUTF8Line() ?: break
    }
}
