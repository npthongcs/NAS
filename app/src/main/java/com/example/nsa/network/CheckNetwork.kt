package com.example.nsa.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.io.IOException

class CheckNetwork {

    companion object {
        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        }

        private fun isOnline(): Boolean {
            val runtime: Runtime = Runtime.getRuntime()
            try {
                val ipProcess: Process = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
                val exitValue = ipProcess.waitFor()
                return exitValue == 0
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return false
        }

        fun isConnected(context: Context): Boolean {
            if (!isNetworkAvailable(context)) {
                Toast.makeText(context, "Unable connect to Network", Toast.LENGTH_SHORT).show()
            } else if (!isOnline()) {
                Toast.makeText(context, "Unable connect to internet", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("Check network", "connect to network and internet successful")
                return true
            }
            return false
        }
    }

}