package com.kgurgul.cpuinfo.data.provider

import android.os.Build
import timber.log.Timber
import java.io.RandomAccessFile
import javax.inject.Inject

class CpuDataProvider @Inject constructor(
    private val nativeProvider: CpuDataNativeProvider
) {

    fun getAbi(): String {
        return Build.SUPPORTED_ABIS[0]
    }

    fun getCpuName(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Build.SOC_MODEL != Build.UNKNOWN) {
            return if (Build.SOC_MANUFACTURER != Build.UNKNOWN)
                "${Build.SOC_MANUFACTURER} ${Build.SOC_MODEL}"
            else
                Build.SOC_MODEL
        }

        return nativeProvider.getCpuName()
    }

    fun getNumberOfCores(): Int {
        return Runtime.getRuntime().availableProcessors()
    }

    /**
     * Checking frequencies directories and return current value if exists (otherwise we can
     * assume that core is stopped - value -1)
     */
    fun getCurrentFreq(coreNumber: Int): Long {
        val currentFreqPath = "${CPU_INFO_DIR}cpu$coreNumber/cpufreq/scaling_cur_freq"
        return try {
            RandomAccessFile(currentFreqPath, "r").use { it.readLine().toLong() / 1000 }
        } catch (e: Exception) {
            Timber.e("getCurrentFreq() - cannot read file")
            -1
        }
    }

    /**
     * Read max/min frequencies for specific [coreNumber]. Return [Pair] with min and max frequency
     * or [Pair] with -1.
     */
    fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        val minPath = "${CPU_INFO_DIR}cpu$coreNumber/cpufreq/cpuinfo_min_freq"
        val maxPath = "${CPU_INFO_DIR}cpu$coreNumber/cpufreq/cpuinfo_max_freq"
        return try {
            val minMhz = RandomAccessFile(minPath, "r").use { it.readLine().toLong() / 1000 }
            val maxMhz = RandomAccessFile(maxPath, "r").use { it.readLine().toLong() / 1000 }
            Pair(minMhz, maxMhz)
        } catch (e: Exception) {
            Timber.e("getMinMaxFreq() - cannot read file")
            Pair(-1, -1)
        }
    }

    companion object {
        private const val CPU_INFO_DIR = "/sys/devices/system/cpu/"
    }
}