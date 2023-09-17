package com.longkd.base_android.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * @Author: longkd
 * @Since: 10:45 - 12/08/2023
 */
internal class NetworkConnectivityObserver(context: Context) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observer(): Flow<ConnectivityObserver.Status> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                launch { send(ConnectivityObserver.Status.AVAILABLE) }
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                launch { send(ConnectivityObserver.Status.LOSING) }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                launch { send(ConnectivityObserver.Status.LOST) }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                launch { send(ConnectivityObserver.Status.UNAVAILABLE) }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
            connectivityManager.registerNetworkCallback(networkRequest, callback)

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(callback)
            }
        }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}