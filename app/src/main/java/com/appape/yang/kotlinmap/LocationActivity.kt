package com.appape.yang.kotlinmap

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*


class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mLocationRequest: LocationRequest? = null

    private val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    private var latitude = 0.0
    private var longitude = 0.0

    private lateinit var mGoogleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.location) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    protected fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            setInterval(UPDATE_INTERVAL)
            setFastestInterval(FASTEST_INTERVAL)
        }

        // initialize locationo setting request builder object
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        // initialize location service object
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        // call register location listner
        registerLocationListner()
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap;

        if (mGoogleMap != null) {
            mGoogleMap!!.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("Current Location"))
        }

    }

    private fun registerLocationListner() {
        // initialize location callback object
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.getLastLocation())
            }
        }
        // add permission if android version is greater then 23
        if(Build.VERSION.SDK_INT >= 23 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
        }
    }

    private fun onLocationChanged(location: Location) {
        // create message for toast with updated latitude and longitudefa
        var msg = "Updated Location: " + location.latitude  + " , " +location.longitude

        // show toast message with updated location
        //Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
        val location = LatLng(location.latitude, location.longitude)
        mGoogleMap!!.clear()
        mGoogleMap!!.addMarker(MarkerOptions().position(location).title("Current Location"))
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        Toast.makeText(this, "Debug XXXX "+msg, Toast.LENGTH_LONG).show()
    }

    private fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions()
            return false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"),1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
            if (permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION ) {
                registerLocationListner()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.getItemId()) {
            R.id.whole_map -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.location -> {
                val intent = Intent(this, LocationActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


}
