import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

const val DEFAULT_PORT = 8080
const val DEFAULT_RESPONSE_DATA_SIZE_BYTES = 1024
const val CONNECTION_TIMEOUT_SECONDS = 45


fun main(args: Array<String>) = runBlocking<Unit> {
    val parser = ArgParser("ShredBench", prefixStyle = ArgParser.OptionPrefixStyle.LINUX)

    val port by parser.option(
        ArgType.Int,
        shortName = "p",
        fullName = "port",
        description = "The port to handle HTTP requests on.",
    ).default(DEFAULT_PORT)
    val latency by parser.option(
        ArgType.Int,
        shortName = "l",
        fullName = "latency",
        description = "A static latency value to inject before returning a response, in milliseconds."
    )
    val responseDataSize by parser.option(
        ArgType.Int,
        shortName = "d",
        fullName = "data-size",
        description = "The size of the response body to be returned, in bytes."
    ).default(DEFAULT_RESPONSE_DATA_SIZE_BYTES)
    val verbose by parser.option(
        ArgType.Boolean,
        shortName = "v",
        fullName = "verbose",
        description = "Enable request logging on stdout. Note: this will reduce request handling throughput, but " +
                "it's useful for debugging",
    ).default(false)

    parser.parse(args)
    println()
    println("Running ShredBench with the following options:")
    println("Running on port $port")
    if (latency != null) {
        println("Injecting $latency milliseconds of delay before responding to each request")
    }
    println("Response size: $responseDataSize bytes")
    println()

    runServer(port, latency?.toLong(), responseDataSize, verbose)
}

fun runServer(port: Int, latencyMs: Long? = null, responseDataSizeBytes: Int = 1000, verbose: Boolean = false) = runBlocking {
    embeddedServer(CIO, port = port, configure = {
        connectionIdleTimeoutSeconds = CONNECTION_TIMEOUT_SECONDS
    }) {
        routing {
            get("/{param...}") {
                if (verbose) {
                    val headersStr = call.request.headers.entries().joinToString(", ") { (name, values) ->
                        "'$name: ${values.joinToString(", ")}'"
                    }
                    println("GET ${call.request.uri} Headers: $headersStr")
                }

                if (latencyMs != null) {
                    delay(latencyMs)
                }

                call.respond("s".repeat(responseDataSizeBytes))
            }
        }
    }.start(wait = true)
}
