package com.playlnw.bankmonitor

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.Date

/**
 * Service สำหรับฟัง Notifications จากทุก App
 * จะทำงานเมื่อได้รับ notification ใหม่
 */
class BankNotificationListener : NotificationListenerService() {
    
    companion object {
        private const val TAG = "BankNotifListener"
    }
    
    /**
     * เมื่อมี notification ใหม่เข้ามา
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        if (sbn == null) return
        
        try {
            // ดึงข้อมูลจาก notification
            val packageName = sbn.packageName
            val notification = sbn.notification
            val extras = notification?.extras
            
            // ดึงข้อความจาก notification
            val title = extras?.getString("android.title") ?: ""
            val text = extras?.getCharSequence("android.text")?.toString() ?: ""
            val bigText = extras?.getCharSequence("android.bigText")?.toString() ?: ""
            
            // รวมข้อความทั้งหมด
            val fullText = "$title\n$text\n$bigText".trim()
            
            Log.d(TAG, "Notification from: $packageName")
            Log.d(TAG, "Text: $fullText")
            
            // ตรวจสอบว่าเป็น notification จากธนาคารหรือไม่
            if (isBankNotification(packageName, fullText)) {
                Log.i(TAG, "✅ Bank notification detected!")
                
                // สร้าง Transaction object
                val transaction = createTransaction(packageName, fullText)
                
                // Parse ข้อมูล
                val parsed = BankParser.parse(fullText)
                
                // ถ้า parse สำเร็จ → ส่งไป API
                if (parsed.bankName != null && parsed.amount != null) {
                    Log.i(TAG, "✅ Parsed: ${parsed.bankName} - ฿${parsed.amount}")
                    
                    // TODO: บันทึกลง local database
                    // TODO: ส่งไป API
                    sendToApi(transaction, parsed)
                } else {
                    Log.w(TAG, "⚠️ Cannot parse notification")
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error processing notification", e)
        }
    }
    
    /**
     * เมื่อ notification ถูกลบ
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d(TAG, "Notification removed: ${sbn?.packageName}")
    }
    
    /**
     * ตรวจสอบว่าเป็น notification จากธนาคารหรือไม่
     */
    private fun isBankNotification(packageName: String, text: String): Boolean {
        // เช็ค package name (LINE)
        if (packageName == ApiConfig.LINE_PACKAGE) {
            // เช็คว่ามีคำที่เกี่ยวกับธนาคารหรือไม่
            val hasBankKeyword = ApiConfig.SUPPORTED_BANKS.any { 
                text.contains(it, ignoreCase = true) 
            }
            
            val hasMoneyKeyword = text.contains("รับเงิน", ignoreCase = true) ||
                                 text.contains("ได้รับ", ignoreCase = true) ||
                                 text.contains("โอนเงิน", ignoreCase = true) ||
                                 text.contains("บาท", ignoreCase = true)
            
            return hasBankKeyword && hasMoneyKeyword
        }
        
        // TODO: เพิ่มการตรวจสอบ SMS
        
        return false
    }
    
    /**
     * สร้าง BankTransaction object
     */
    private fun createTransaction(packageName: String, text: String): BankTransaction {
        val deviceId = android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )
        
        val type = when (packageName) {
            ApiConfig.LINE_PACKAGE -> "LINE"
            else -> "OTHER"
        }
        
        return BankTransaction(
            type = type,
            sourceApp = packageName,
            rawText = text,
            deviceId = deviceId,
            receivedAt = Date()
        )
    }
    
    /**
     * ส่งข้อมูลไป API
     */
    private fun sendToApi(transaction: BankTransaction, parsed: BankParser.ParsedData) {
        // TODO: Implement API call
        Log.i(TAG, "Sending to API: ${transaction.toApiJson()}")
        
        // ตัวอย่าง:
        // ApiService.sendNotification(transaction) { success ->
        //     if (success) {
        //         Log.i(TAG, "✅ Sent to API successfully")
        //     } else {
        //         Log.e(TAG, "❌ Failed to send to API")
        //     }
        // }
    }
}
