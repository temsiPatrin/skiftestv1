package com.example.android.skiftestv1

import android.util.Log
import com.example.android.skiftestv1.Presenter.Companion.LAT_JSON
import com.example.android.skiftestv1.Presenter.Companion.LNG_JSON
import com.example.android.skiftestv1.Presenter.Companion.TIME_JSON
import com.example.android.skiftestv1.entites.LineTrack
import com.example.android.skiftestv1.network.Api
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Presenter : MvpContract.Presenter {
    companion object{
        const val TIME_JSON = 0
        const val LAT_JSON = 2
        const val LNG_JSON = 1
    }

    private var view: MvpContract.View? = null
    private var jsonString: String = ""
    private lateinit var listLineTrack: ArrayList<LineTrack>
    var isPlayed: Boolean = false

    override fun attach(view: MvpContract.View) {
        this.view = view
    }

    override fun loadJson() {
        val job = Job()
        CoroutineScope(Dispatchers.Main + job).launch {
            view?.showProgress()
            var listPointed: MutableIterable<LatLng>? = null
            withContext(Dispatchers.IO) {
                jsonString = Api.retrofitService.getProperties().await()
                listPointed = getPointList(jsonString) as MutableIterable<LatLng>
                listLineTrack = getLineList(jsonString) as ArrayList<LineTrack>
            }
            view?.drawGraph(listPointed!!)
            view?.hideProgress()
        }
    }

    var start: LatLng = LatLng(0.0, 0.0)
    var end: LatLng = LatLng(0.0, 0.0)
    var speed: Double = 0.0
    var interval: Float = 0F
    var t: Float = 0F
    var numberLine: Int = 0
    var latP: Double = 0.0
    var lngP: Double = 0.0

    override fun startMap() {
        var job = Job()
        val coroutineScope = CoroutineScope(Dispatchers.Main + job)

        coroutineScope.launch {
            Log.d("listTrack", listLineTrack.toString())
            for (i in numberLine until listLineTrack.size) {
                if (isPlayed) {
                    if (t > 1 || t == 0F) {
                        start = listLineTrack[i].startCoordinate
                        end = listLineTrack[i].endCoordinate
                        speed = listLineTrack[i].speedKmPerH
                        interval = (16F * 10) / listLineTrack[i].time
                        t = (16F * 10) / listLineTrack[i].time
                    }
                    view?.showSpeed(speed.toFloat().toString().substringBefore("."))
                    while (t < 1 && isPlayed) {
                        withContext(Dispatchers.IO) {
                            latP = t * end.latitude + (1 - t) * start.latitude
                            lngP = t * end.longitude + (1 - t) * start.longitude
                            Log.d("lat", start.latitude.toString())
                            Log.d("lng", start.longitude.toString())
                            t += interval
                            delay(16)
                        }
                        view?.moveMarker(LatLng(latP,lngP))
                    }
                    numberLine++
                } else {
                    job.cancel()
                    break
                }
            }
        }
    }

    override fun playStop() {
        if (isPlayed) {
            isPlayed = false
            view?.btnPlay()

        } else {
            isPlayed = true
            startMap()
            view?.btnStop()

        }
    }

    override fun detach() {
        this.view = null

    }
}

fun getPointList(jsonString: String): List<LatLng> {
    val pointsArray: MutableList<LatLng> = arrayListOf()
    val jsonArray = JSONArray(jsonString)
    for (i in 0 until jsonArray.length()) {
        val jsonArray1 = jsonArray.getJSONArray(i)
        val lat: Double = jsonArray1.getDouble(LAT_JSON)
        val lng: Double = jsonArray1.getDouble(LNG_JSON)
        pointsArray.add(LatLng(lat, lng))
    }
    return pointsArray
}

fun getLineList(jsonString: String): List<LineTrack> {
    val lineArray: MutableList<LineTrack> = arrayListOf()
    val jsonArray = JSONArray(jsonString)
    for (i in 0..jsonArray.length() - 2) {
        val jsonArray1 = jsonArray.getJSONArray(i)
        val lat1: Double = jsonArray1.getDouble(LAT_JSON)
        val lng1: Double = jsonArray1.getDouble(LNG_JSON)
        val date1: Date = parseDate(jsonArray1.getString(TIME_JSON))
        val jsonArray2 = jsonArray.getJSONArray(i + 1)
        val lat2: Double = jsonArray2.getDouble(LAT_JSON)
        val lng2: Double = jsonArray2.getDouble(LNG_JSON)
        val date2: Date = parseDate(jsonArray2.getString(TIME_JSON))
        lineArray.add(
            LineTrack(
                startTime = date1,
                endTime = date2,
                startCoordinate = LatLng(lat1, lng1),
                endCoordinate = LatLng(lat2, lng2)
            )
        )
    }
    return lineArray
}

fun parseDate(dt: String): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return formatter.parse(dt)
}