package dev.asdevs.signalr_flutter

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** SignalRFlutterPlugin */
public class SignalRHandler : MethodCallHandler {
        
    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            CallMethod.ConnectToServer.value -> {
                val arguments = call.arguments as Map<*, *>
                @Suppress("UNCHECKED_CAST")
                SignalR.connectToServer(
                        arguments["baseUrl"] as String,
                        arguments["hubName"] as String,
                        arguments["queryString"] as String,
                        arguments["headers"] as? Map<String, String> ?: emptyMap(),
                        arguments["transport"] as Int,
                        arguments["hubMethods"] as? List<String> ?: emptyList(),
                        result)
            }
            CallMethod.Reconnect.value -> {
                SignalR.reconnect(result)
            }
            CallMethod.Stop.value -> {
                SignalR.stop(result)
            }
            CallMethod.ListenToHubMethod.value -> {
                if (call.arguments is String) {
                    val methodName = call.arguments as String
                    SignalR.listenToHubMethod(methodName, result)
                } else {
                    result.error("Error", "Cast to String Failed", "")
                }
            }
            CallMethod.InvokeServerMethod.value -> {
                val arguments = call.arguments as Map<*, *>
                @Suppress("UNCHECKED_CAST")
                SignalR.invokeServerMethod(arguments["methodName"] as String, arguments["arguments"] as? List<Any>
                        ?: emptyList(), result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

  
}


