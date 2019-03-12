package io.ktor.client.tests.utils.tests

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.atomicfu.*
import java.util.concurrent.atomic.*

val counter = AtomicInteger(0)

fun Application.cacheTestServer() {
    routing {
        route("/cache") {
            get("/no-cache") {
                val value = counter.incrementAndGet()
                call.response.cacheControl(CacheControl.NoCache(null))
                call.respondText("$value")
            }
            get("/no-store") {
                val value = counter.incrementAndGet()
                call.response.cacheControl(CacheControl.NoStore(null))
                call.respondText("$value")
            }
            get("/max-age") {
                val value = counter.incrementAndGet()
                call.response.cacheControl(CacheControl.MaxAge(1))
                call.respondText("$value")
            }

            /**
             * Return same etag for first 2 responses.
             */
            get("/etag") {
                var current = counter.get()

                current = counter.incrementAndGet()
                @Suppress("DEPRECATION")
                call.withETag("0") {
                    call.respondText(current.toString())
                }
            }

            get("/public") {
                call.response.cacheControl(CacheControl.MaxAge(60))
                call.respondText("public")
            }
            get("/private") {
                call.response.cacheControl(CacheControl.MaxAge(60, visibility = CacheControl.Visibility.Private))
                call.response.cacheControl(CacheControl.NoCache(CacheControl.Visibility.Private))
                call.respondText("private")
            }
        }
    }
}
