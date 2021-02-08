package dev.asdevs.signalr_flutter

import android.os.Looper
import io.flutter.plugin.common.MethodChannel.Result
import microsoft.aspnet.signalr.client.*
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import microsoft.aspnet.signalr.client.transport.LongPollingTransport
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport
object SignalR1 {
    private lateinit var connection: HubConnection
    private lateinit var hub: HubProxy

    fun connectToServer(url: String, hubName: String, queryString: String, headers: Map<String, String>, transport: Int, hubMethods: List<String>, result: Result) {
        try {
            connection = if (queryString.isEmpty()) {
                HubConnection(url)
            } else {
                HubConnection(url, queryString, true, Logger { _: String, _: LogLevel ->
                })
            }

            if (headers.isNotEmpty()) {
                val cred = Credentials() { request ->
                    request.headers = headers
                }
                connection.credentials = cred
            }
            hub = connection.createHubProxy(hubName)

            hubMethods.forEach { methodName ->
                hub.on(methodName, { res ->
                    android.os.Handler(Looper.getMainLooper()).post {
                        SignalRFlutterPlugin.channel.invokeMethod("NewMessage1", listOf(methodName, res))
                    }
                }, Any::class.java)
            }

            connection.connected {
                android.os.Handler(Looper.getMainLooper()).post {
                    SignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus1", connection.state.name)
                }
            }

            connection.reconnected {
                android.os.Handler(Looper.getMainLooper()).post {
                    SignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus1", connection.state.name)
                }
            }

            connection.reconnecting {
                android.os.Handler(Looper.getMainLooper()).post {
                    SignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus1", connection.state.name)
                }
            }

            connection.closed {
                android.os.Handler(Looper.getMainLooper()).post {
                    SignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus1", connection.state.name)
                }
            }

            connection.connectionSlow {
                android.os.Handler(Looper.getMainLooper()).post {
                    SignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus1", "Slow")
                }
            }

            connection.error { handler ->
                android.os.Handler(Looper.getMainLooper()).post {
                    SignalRFlutterPlugin.channel.invokeMethod("ConnectionStatus1", handler.localizedMessage)
                }
            }

            when (transport) {
                1 -> connection.start(ServerSentEventsTransport(connection.logger))
                2 -> connection.start(LongPollingTransport(connection.logger))
                else -> {
                    connection.start()
                }
            }

            result.success(true)
        } catch (ex: Exception) {
            result.error("Error", ex.localizedMessage, null)
        }
    }

    fun reconnect(result: Result) {
        try {
            connection.start()
        } catch (ex: Exception) {
            result.error(ex.localizedMessage, ex.stackTrace.toString(), null)
        }
    }

    fun stop(result: Result) {
        try {
            connection.stop()
        } catch (ex: Exception) {
            result.error(ex.localizedMessage, ex.stackTrace.toString(), null)
        }
    }

    fun listenToHubMethod(methodName: String, result: Result) {
        try {
            hub.on(methodName, { res ->
                android.os.Handler(Looper.getMainLooper()).post {
                    SignalRFlutterPlugin.channel.invokeMethod("NewMessage1", listOf(methodName, res))
                }
            }, Any::class.java)
        } catch (ex: Exception) {
            result.error("Error", ex.localizedMessage, null)
        }
    }

    fun invokeServerMethod(methodName: String, args: List<Any>, result: Result) {
        try {
            val res: SignalRFuture<Any> = hub.invoke(Any::class.java, methodName, *args.toTypedArray())

            res.done { msg: Any? ->
                android.os.Handler(Looper.getMainLooper()).post {
                    result.success(msg)
                }
            }

            res.onError { throwable ->
                android.os.Handler(Looper.getMainLooper()).post {
                    result.error("Error", throwable.localizedMessage, null)
                }
            }
        } catch (ex: Exception) {
            result.error("Error", ex.localizedMessage, null)
        }
    }
}