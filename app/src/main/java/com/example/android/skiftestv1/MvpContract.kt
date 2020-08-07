package com.example.android.skiftestv1


import com.google.android.gms.maps.model.LatLng

interface MvpContract {
    interface View{
        fun showProgress()
        fun hideProgress()
        fun drawGraph(listPointed: MutableIterable<LatLng>)
        fun moveMarker(coordinate : LatLng)
        fun showSpeed(speed:String)
        fun btnStop()
        fun btnPlay()

    }
    interface Presenter{
        fun attach(view: View)
        fun loadJson()
        fun startMap()
        fun playStop()
        fun detach()
    }
}