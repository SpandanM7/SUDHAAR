package com.sudhaar.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform