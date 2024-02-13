package com.kgurgul.cpuinfo.domain.model

data class GpuData(
    val vulkanVersion: String,
    val glesVersion: String,
    val glVendor: String?,
    val glRenderer: String?,
    val glExtensions: String?,
    val frequency: Frequency
) {
    data class Frequency(
        val min: Long,
        val max: Long,
        val current: Long
    )
}