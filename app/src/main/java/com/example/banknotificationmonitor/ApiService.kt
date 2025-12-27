package com.playlnw.bankmonitor

import android.util.Log
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object ApiService {
    
    private const val TAG = "ApiService"
    
    fun sendNotification(
        transaction: BankTransaction,
        callback: (Boolean, String?) -> Unit
    ) {
        thread {
            try {
                val url = URL("${ApiConfig.BASE_URL}${ApiConfig.NOTIFICATION_ENDPOINT}")
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("X-API-Key", ApiConfig.API_KEY)
                connection.connectTimeout = 30000
                connection.readTimeout = 30000
                connection.doOutput = true
                
                val json = JSONObject().apply {
                    put("type", transaction.type)
                    put("app", transaction.sourceApp)
                    put("text", transaction.rawText)
                    put("timestamp", transaction.receivedAt.time / 1000)
                    put("device_id", transaction.deviceId)
                }
                
                Log.d(TAG, "Sending to API...")
                
                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(json.toString())
                writer.flush()
                writer.close()
                
                val responseCode = connection.responseCode
                val responseMessage = if (responseCode == 200 || responseCode == 201) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                }
                
                Log.d(TAG, "Response: $responseCode")
                
                connection.disconnect()
                
                if (responseCode == 200 || responseCode == 201) {
                    callback(true, responseMessage)
                } else {
                    callback(false, "HTTP $responseCode")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error", e)
                callback(false, e.message)
            }
        }
    }
    
    fun testConnection(callback: (Boolean, String) -> Unit) {
        thread {
            try {
                val url = URL("${ApiConfig.BASE_URL}${ApiConfig.NOTIFICATION_ENDPOINT}")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                val responseCode = connection.responseCode
                connection.disconnect()
                
                if (responseCode == 200 || responseCode == 405) {
                    callback(true, "API OK")
                } else {
                    callback(false, "HTTP $responseCode")
                }
            } catch (e: Exception) {
                callback(false, e.message ?: "Error")
            }
        }
    }
}
