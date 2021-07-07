package com.example.nsa.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsa.ApiService
import com.example.nsa.RetroInstance
import com.example.nsa.model.Docs
import com.example.nsa.model.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository {
    private val apiKey = "GKKclywskYxAotfUsGb7tgaNqBIgWQl5"
    private var listDocs: ArrayList<Docs> = ArrayList()
    private var responseWrapperLiveData: MutableLiveData<ResponseWrapper> = MutableLiveData()
    private val retroInstance: ApiService = RetroInstance.getRetroInstance().create(ApiService::class.java)

    fun responseWrapperLiveDataObserver(): MutableLiveData<ResponseWrapper>{
        return responseWrapperLiveData
    }

    fun getListDocs(): ArrayList<Docs>{
        return listDocs
    }

    fun responseWrapperCallAPI(query: String?, beginDate: String?, sort: String?, newsDesk: String?, page: Int, isQuery: Boolean){
        val call = retroInstance.getResponse(query,beginDate,sort,newsDesk,page,apiKey)
        call.enqueue(object : Callback<ResponseWrapper>{
            override fun onResponse(
                call: Call<ResponseWrapper>,
                response: Response<ResponseWrapper>
            ) {
                if (response.isSuccessful) {
                    if (isQuery) listDocs.clear()
                    response.body()?.let { listDocs.addAll(it.response.docs) }
                    responseWrapperLiveData.postValue(response.body())
                }
                else responseWrapperLiveData.postValue(null)
            }

            override fun onFailure(call: Call<ResponseWrapper>, t: Throwable) {
                responseWrapperLiveData.postValue(null)
            }

        })
    }

}