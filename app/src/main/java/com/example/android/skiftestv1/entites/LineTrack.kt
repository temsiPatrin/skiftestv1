package com.example.android.skiftestv1.entites

import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LineTrack(
    val startTime: Date,
    val endTime: Date,
    val startCoordinate: LatLng,
    val endCoordinate: LatLng
) {
    val time: Long = createTime()
    val distanceKm: Float = (distFrom()) / 1000
    val speedKmPerH = distanceKm / (time * 3.6)

    private fun createTime(): Long = this.endTime.time - this.startTime.time

    private fun distFrom(): Float {
        val earthRadius = 6371000.0 //meters
        val lat1 = this.startCoordinate.latitude
        val lng1 = this.startCoordinate.longitude
        val lat2 = this.endCoordinate.latitude
        val lng2 = this.endCoordinate.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1.toDouble())) * cos(Math.toRadians(lat2.toDouble())) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c).toFloat()
    }
}