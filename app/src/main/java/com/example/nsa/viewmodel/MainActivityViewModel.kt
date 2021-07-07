package com.example.nsa.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsa.model.ResponseWrapper
import com.example.nsa.repository.MainRepository

class MainActivityViewModel : ViewModel() {
    private val mainRepository = MainRepository()
    val listDocs = mainRepository.getListDocs()

    fun getResponseWrapperLiveDataObserver(): MutableLiveData<ResponseWrapper>{
        return mainRepository.responseWrapperLiveDataObserver()
    }

    fun fetchResponseWrapper(query: String?, beginDate: String?, sort: String?, newsDesk: String?, page: Int, isQuery: Boolean){
        mainRepository.responseWrapperCallAPI(query,beginDate,sort,newsDesk,page,isQuery)
    }

}