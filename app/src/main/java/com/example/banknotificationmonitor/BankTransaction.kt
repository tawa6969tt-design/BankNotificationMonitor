package com.playlnw.bankmonitor

import java.util.Date

/**
 * Data Model สำหรับข้อมูลธุรกรรมธนาคาร
 * ใช้เก็บข้อมูลที่ parse จาก notification
 */
data class BankTransaction(
    // ID ของ transaction (สำหรับ local database)
    val id: Long = 0,
    
    // ประเภท notification (LINE, SMS, BANK_APP)
    val type: String,
    
    // ชื่อ app ต้นทาง (เช่น jp.naver.line.android)
    val sourceApp: String,
    
    // ข้อความดิบที่ได้จาก notification
    val rawText: String,
    
    // ชื่อธนาคาร (กสิกรไทย, ไทยพาณิชย์, etc.)
    val bankName: String? = null,
    
    // ยอดเงิน
    val amount: Double? = null,
    
    // เลขบัญชีผู้โอน
    val senderAccount: String? = null,
    
    // ชื่อผู้โอน
    val senderName: String? = null,
    
    // Reference / เลขอ้างอิง
    val reference: String? = null,
    
    // วันเวลาที่ทำธุรกรรม
    val transactionDate: Date? = null,
    
    // Device ID ของมือถือ
    val deviceId: String,
    
    // วันเวลาที่รับ notification
    val receivedAt: Date = Date(),
    
    // สถานะการส่งไป API
    val synced: Boolean = false,
    
    // Error message (ถ้ามี)
    val errorMessage: String? = null
) {
    /**
     * แปลงเป็น JSON สำหรับส่งไป API
     */
    fun toApiJson(): String {
        return """
            {
                "type": "$type",
                "app": "$sourceApp",
                "text": "${rawText.replace("\"", "\\\"").replace("\n", "\\n")}",
                "timestamp": "${receivedAt.time / 1000}",
                "device_id": "$deviceId"
            }
        """.trimIndent()
    }
    
    /**
     * ตรวจสอบว่า parse สำเร็จหรือไม่
     */
    fun isParsed(): Boolean {
        return bankName != null && amount != null
    }
}
