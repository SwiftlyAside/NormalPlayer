package com.ngsc.normal_player

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Environment
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    var CHANNEL = "android_app_retain"
    var mmr = MediaMetadataRetriever()

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            run {
                val arguments = call.arguments<Map<String, Any>>()
                if (call.method.equals("sendToBackground")) moveTaskToBack(true)

                if (call.method.equals("getStoragePath")) {
                    val path = Environment.getDataDirectory().toString()
                    result.success(path)
                }

                if (call.method.equals("getMetaData")) {
                    val filepath = arguments["filepath"] as String
                    println(filepath)
                    val l = ArrayList<Any?>()
                    mmr.setDataSource(filepath)
                    l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE))
                    l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST))
                    try {
                        l.add(mmr.embeddedPicture)
                    } catch (e: Exception) {
                        l.add("")
                    }

                    l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM))
                    l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER))
                    l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
                    result.success(l)
                }

                if (call.method.equals("getSdCardPath")) {
                    var removableStoragePath: String? = null
                    try {
                        removableStoragePath = externalCacheDirs[1].toString()
                    } catch (e: Exception) {
                        println(e)
                    }
                    result.success(removableStoragePath)
                }

                // getColor 는 flutter에서 하십쇼~
            }
        }
    }
    fun getDominantColor(bitmap: Bitmap): Int {
        val newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true)
        val color = newBitmap.getPixel(0, 0)
        newBitmap.recycle()
        return color
    }
}
