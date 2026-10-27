package com.nbs.banking.core.tracer

import com.google.firebase.Firebase
import com.google.firebase.perf.metrics.Trace
import com.google.firebase.perf.performance
import kotlin.time.Duration.Companion.milliseconds
import timber.log.Timber

object TracerManager {
    private val tracers = mutableListOf<Pair<Trace, Long>>()
    private val localTracers = mutableMapOf<String, Long>()

    fun startTrace(withName: String, attributes: Map<String, String>?) {
        val trace = Firebase.performance.newTrace(withName).apply {
            attributes?.forEach { (k, v) -> this.putAttribute(k, v) }
        }
        if (tracers.any { it.first.name == withName }) {
            Timber.e("Tracer $withName already started before")
        } else {
            tracers.add(Pair(trace, System.currentTimeMillis()))
            trace.start()
            Timber.i("Tracer $withName is starting")
        }
    }

    fun endTrace(withName: String) {
        val startTime: Long? = tracers.find { it.first.name == withName }?.second

        val trace = tracers.find { it.first.name == withName }
        trace?.first?.stop()
        tracers.remove(trace)

        Timber.i("Tracer $withName is stopping")
        startTime?.let {
            val duration = System.currentTimeMillis() - startTime
            Timber.i("Tracer $withName Total duration ${duration.milliseconds}")
        }
    }

    fun startLocalTrace(withName: String) {
        if (localTracers.containsKey(withName)) {
            Timber.e("Local tracer $withName already started before")
        } else {
            localTracers[withName] = System.currentTimeMillis()
            Timber.i("Local tracer $withName is starting")
        }
    }

    fun endLocalTrace(withName: String): Long? {
        val startTime: Long? = localTracers[withName]
        return if (startTime != null) {
            localTracers.remove(withName)
            val duration = System.currentTimeMillis() - startTime
            val durationInMillis = duration.milliseconds.inWholeMilliseconds
            Timber.i("Local tracer $withName is stopping, Total duration $durationInMillis ms")
            durationInMillis
        } else {
            Timber.e("Local tracer $withName was not started")
            null
        }
    }
}
