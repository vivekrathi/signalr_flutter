package dev.asdevs.signalr_flutter

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** SignalRFlutterPlugin */
public class SignalRFlutterPlugin : FlutterPlugin {
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "signalR")
        channel.setMethodCallHandler(SignalRHandler());
        channel1 = MethodChannel(flutterPluginBinding.binaryMessenger, "signalR1")
        channel1.setMethodCallHandler(SignalRHandler1());
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    companion object {
        lateinit var channel: MethodChannel
        lateinit var channel1: MethodChannel

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "signalR")
            channel.setMethodCallHandler(SignalRHandler())
              val channel1 = MethodChannel(registrar.messenger(), "signalR1")
            channel1.setMethodCallHandler(SignalRHandler1())
        }
    }   
   

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        channel1.setMethodCallHandler(null)
    }
}


