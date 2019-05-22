package kin.sdk

import android.util.Log
import kin.sdk.IntegConsts.URL_CREATE_ACCOUNT
import kin.sdk.IntegConsts.URL_FUND
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Fake on board for integration test, support creating and funding accounts on stellar test net
 */
internal class FakeKinOnBoard @Throws(IOException::class)
constructor() {

    @Throws(Exception::class)
    fun createAccount(destinationAccount: String) {
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        Log.d("AMITAI", "destinationAccount = $destinationAccount")
        val request = Request.Builder()
                .url(String.format(URL_CREATE_ACCOUNT, destinationAccount)).build()
        Thread.sleep(10000)
        client.newCall(request).execute()?.let {
            if (it.body() == null || it.code() != 200) {
                Log.d("AMITAI", "createAccount error, error code = ${it.code()}, message = ${it.message()}")
            } else {
                Log.d("AMITAI", "code = ${it.code()} , it.body() == null = ${it.body() == null}")
            }
        }
    }

    fun fundWithKin(destinationAccount: String, amount: String) {
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        Log.d("AMITAI", "destinationAccount = $destinationAccount")
        val request = Request.Builder()
                .url(String.format(URL_FUND + amount, destinationAccount))
                .get()
                .build()
        client.newCall(request).execute()?.let {
            if (it.body() == null || it.code() != 200) {
                Log.d("AMITAI", "fundWithKin error, error code = ${it.code()}, message = ${it.message()}")
            } else {
                Log.d("AMITAI", "code = ${it.code()} , it.body() == null = ${it.body() == null}")
            }
        }

    }
}

