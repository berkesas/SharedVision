package com.scinforma.sharedvision

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform