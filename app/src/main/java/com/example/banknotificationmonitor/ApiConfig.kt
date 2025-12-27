package com.playlnw.bankmonitor

/**
 * API Configuration
 * เก็บค่า Config สำหรับเชื่อมต่อ Backend
 */
object ApiConfig {
    // Base URL ของ Backend
    const val BASE_URL = "https://playlnw.com/api/"
    
    // API Key สำหรับยืนยันตัวตน
    const val API_KEY = "bank_notif_5c0b3fa6c71b52b120b838f41ddd1be6"
    
    // Endpoint สำหรับส่ง notification
    const val NOTIFICATION_ENDPOINT = "bank-notifications"
    
    // ธนาคารที่รองรับ
    val SUPPORTED_BANKS = listOf(
        "กสิกรไทย",
        "ไทยพาณิชย์",
        "กรุงเทพ",
        "กรุงไทย",
        "กรุงศรี",
        "ทหารไทย",
        "ออมสิน"
    )
    
    // Package name ของ LINE
    const val LINE_PACKAGE = "jp.naver.line.android"
    
    // Timeout สำหรับ API call (วินาที)
    const val TIMEOUT_SECONDS = 30L
}
