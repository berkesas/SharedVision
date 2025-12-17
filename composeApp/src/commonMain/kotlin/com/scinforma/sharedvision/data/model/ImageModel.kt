package com.scinforma.sharedvision.data.model

data class ImageModel(
    val id: String,
    val name: String,
    val author: String? = null,
    val description: String,
    val version: String,
    val size: Long, // Size in bytes
    val downloadUrl: String,
    val fileName: String,
    val isDownloaded: Boolean = false,
    val downloadProgress: Float = 0f,
    val isDownloading: Boolean = false,
    val publishDate: Long? = null, // Timestamp
    val lastUsed: Long? = null, // Timestamp
    val accuracy: Float? = null, // Model accuracy percentage
    val categories: List<String> = emptyList(), // Classification categories
    val inputSize: Pair<Int, Int>? = null // Input image dimensions (width, height)
)