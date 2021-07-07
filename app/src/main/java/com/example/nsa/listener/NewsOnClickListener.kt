package com.example.nsa.listener

import com.example.nsa.model.Docs

interface NewsOnClickListener {
    fun onItemClick(data: Docs)
}