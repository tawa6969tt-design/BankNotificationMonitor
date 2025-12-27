package com.playlnw.bankmonitor

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ตัว Parse ข้อความจากธนาคาร
 * แยกข้อมูลจาก notification text
 */
object BankParser {
    
    /**
     * Parse ข้อความธนาคาร
     */
    fun parse(text: String): ParsedData {
        val bankName = detectBank(text)
        val amount = extractAmount(text)
        val senderName = extractSenderName(text)
        val senderAccount = extractAccount(text)
        
        return ParsedData(
            bankName = bankName,
            amount = amount,
            senderName = senderName,
            senderAccount = senderAccount
        )
    }
    
    /**
     * ตรวจจับธนาคารจากข้อความ
     */
    private fun detectBank(text: String): String? {
        return when {
            text.contains("กสิกร", ignoreCase = true) -> "กสิกรไทย"
            text.contains("ไทยพาณิชย์", ignoreCase = true) || 
            text.contains("SCB", ignoreCase = true) -> "ไทยพาณิชย์"
            text.contains("กรุงเทพ", ignoreCase = true) || 
            text.contains("BBL", ignoreCase = true) -> "กรุงเทพ"
            text.contains("กรุงไทย", ignoreCase = true) || 
            text.contains("KTB", ignoreCase = true) -> "กรุงไทย"
            text.contains("กรุงศรี", ignoreCase = true) || 
            text.contains("BAY", ignoreCase = true) -> "กรุงศรี"
            text.contains("ทหารไทย", ignoreCase = true) || 
            text.contains("TMB", ignoreCase = true) -> "ทหารไทย"
            text.contains("ออมสิน", ignoreCase = true) -> "ออมสิน"
            else -> null
        }
    }
    
    /**
     * แยกยอดเงินจากข้อความ
     */
    private fun extractAmount(text: String): Double? {
        // Pattern: "รับเงิน 299.00 บาท" หรือ "ได้รับ ฿500.50"
        val patterns = listOf(
            Regex("""รับเงิน\s*([\d,]+\.?\d*)\s*บาท"""),
            Regex("""ได้รับ\s*฿?\s*([\d,]+\.?\d*)"""),
            Regex("""จำนวน\s*([\d,]+\.?\d*)\s*บาท"""),
            Regex("""฿\s*([\d,]+\.?\d*)""")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                val amountStr = match.groupValues[1].replace(",", "")
                return amountStr.toDoubleOrNull()
            }
        }
        
        return null
    }
    
    /**
     * แยกชื่อผู้โอนจากข้อความ
     */
    private fun extractSenderName(text: String): String? {
        // Pattern: "จาก นายทดสอบ ระบบ" หรือ "ผู้โอน: นายทดสอบ"
        val patterns = listOf(
            Regex("""จาก\s+(.+?)(?:\n|$)"""),
            Regex("""ผู้โอน:?\s*(.+?)(?:\n|$)"""),
            Regex("""ชื่อ:?\s*(.+?)(?:\n|$)""")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.groupValues[1].trim()
            }
        }
        
        return null
    }
    
    /**
     * แยกเลขบัญชีจากข้อความ
     */
    private fun extractAccount(text: String): String? {
        // Pattern: "123-4-56789-0" หรือ "xxx1234"
        val patterns = listOf(
            Regex("""(\d{3}-\d{1}-\d{5}-\d{1})"""),
            Regex("""xxx(\d{4})"""),
            Regex("""เลขที่\s*(\S+)""")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        
        return null
    }
    
    /**
     * ผลลัพธ์ที่ parse ได้
     */
    data class ParsedData(
        val bankName: String?,
        val amount: Double?,
        val senderName: String?,
        val senderAccount: String?
    )
}
