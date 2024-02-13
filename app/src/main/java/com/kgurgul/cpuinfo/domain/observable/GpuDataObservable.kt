package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.GpuDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.GpuData
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GpuDataObservable @Inject constructor(
    dispatchersProvider: IDispatchersProvider,
    private val gpuDataProvider: GpuDataProvider
) : MutableInteractor<GpuDataObservable.Params, GpuData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Params) = flow {
        while (true) {
            val (min, max) = gpuDataProvider.getMinMaxFreq()
            val current = gpuDataProvider.getCurrentFreq()
            emit(
                GpuData(
                    gpuDataProvider.getVulkanVersion(),
                    gpuDataProvider.getGlEsVersion(),
                    params.glVendor,
                    params.glRenderer,
                    params.glExtensions,
                    GpuData.Frequency(min, max, current)
                )
            )

            delay(REFRESH_DELAY);
        }
    }

    data class Params(
        val glVendor: String? = null,
        val glRenderer: String? = null,
        val glExtensions: String? = null
    )

    companion object {
        private const val REFRESH_DELAY = 1000L
    }
}