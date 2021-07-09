package com.example.nsa.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsa.ApiService
import com.example.nsa.RetroInstance
import com.example.nsa.model.Docs
import com.example.nsa.model.ResponseWrapper
import com.example.nsa.network.CheckNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository {
    private val apiKey = "GKKclywskYxAotfUsGb7tgaNqBIgWQl5"
    private var responseWrapperLiveData: MutableLiveData<ResponseWrapper> = MutableLiveData()
    private val retroInstance: ApiService =
        RetroInstance.getRetroInstance().create(ApiService::class.java)

    fun responseWrapperLiveDataObserver(): MutableLiveData<ResponseWrapper> {
        return responseWrapperLiveData
    }

    fun responseWrapperCallAPI(
        query: String?,
        beginDate: String?,
        sort: String?,
        newsDesk: String?,
        page: Int
    ) {
        val call = retroInstance.getResponse(query, beginDate, sort, newsDesk, page, apiKey)
        call.enqueue(object : Callback<ResponseWrapper> {
            override fun onResponse(
                call: Call<ResponseWrapper>,
                response: Response<ResponseWrapper>
            ) {
                if (response.isSuccessful) {
                    responseWrapperLiveData.value = response.body()
                } else responseWrapperLiveData.postValue(null)
            }

            override fun onFailure(call: Call<ResponseWrapper>, t: Throwable) {
                responseWrapperLiveData.postValue(null)
            }

        })
    }

}