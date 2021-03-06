/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.features

import io.ktor.client.*
import io.ktor.client.response.*
import kotlinx.coroutines.*
import kotlinx.coroutines.io.*
import kotlinx.coroutines.io.jvm.javaio.*
import java.io.*

internal actual fun HttpClient.platformDefaultTransformers() {
    responsePipeline.intercept(HttpResponsePipeline.Parse) { (info, body) ->
        if (body !is ByteReadChannel) return@intercept
        when (info.type) {
            InputStream::class -> {
                val stream = body.toInputStream(context.coroutineContext[Job])
                proceedWith(HttpResponseContainer(info, stream))
            }
        }
    }
}
