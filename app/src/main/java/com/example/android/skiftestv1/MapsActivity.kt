package com.example.android.skiftestv1

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MvpContract.View {

    companion object{
        private const val STOP_TEXT= "Stop"
        private const val PLAY_TEXT= "Play"
        private const val SKIF_TEXT = "Skif"
        private const val START_LNG = 37.610225
        private const val START_LAT = 55.651365
    }
    private var mMap: GoogleMap? = null
    private var presenter: MvpContract.Presenter? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        presenter = Presenter()
        presenter?.attach(this)
        presenter?.loadJson()
        button.setOnClickListener { presenter?.playStop() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val start = LatLng(START_LAT, START_LNG)
        marker = mMap!!.addMarker(MarkerOptions().position(start).title(SKIF_TEXT))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 17F))
    }

    override fun showProgress() {
        progressBar?.visibility = View.VISIBLE
        button?.visibility = View.GONE
    }

    override fun hideProgress() {
        progressBar?.visibility = View.GONE
        button?.visibility = View.VISIBLE
    }

    override fun drawGraph(listPointed: MutableIterable<LatLng>) {
        mMap?.addPolyline(
            PolylineOptions()
                .color(Color.BLACK)
                .width(2.8F)
                .addAll(listPointed)
        )
    }

    override fun moveMarker(coordinate: LatLng) {
        marker?.position = coordinate
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(coordinate))
    }

    @SuppressLint("SetTextI18n")
    override fun showSpeed(speed: String) {
        textView?.text = "Скорость:\n $speed км/ч"
    }

    override fun btnStop() {
        button?.text = STOP_TEXT
    }

    override fun btnPlay() {
        button?.text = PLAY_TEXT
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detach()
    }


}