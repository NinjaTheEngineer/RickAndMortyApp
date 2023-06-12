package com.ninja.rickandmortyapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService
import com.ninja.rickandmortyapp.api.ApiException
import com.ninja.rickandmortyapp.api.ApiService
import com.ninja.rickandmortyapp.model.Character
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CharacterRepository {
    private var nextPageUrl: String? = null
    private val apiService = Retrofit.Builder()
        .baseUrl("https://rickandmortyapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    suspend fun getInitialCharacters(): List<Character> {
        val response = apiService.getCharacters()
        if (response.isSuccessful) {
            val characterResponse = response.body()
            nextPageUrl = characterResponse?.info?.next
            return characterResponse?.results.orEmpty()
        } else {
            throw ApiException("Error fetching characters")
        }
    }

    suspend fun getAllCharacters(): List<Character> {
        val allCharacters = getInitialCharacters().toMutableList()
        while (nextPageUrl != null) {
            val nextPageCharacters = getNextPageCharacters()
            allCharacters.addAll(nextPageCharacters)
        }
        return allCharacters
    }

    private suspend fun getNextPageCharacters(): List<Character> {
        val url = nextPageUrl
        return if (url != null) {
            val response = apiService.getCharactersByUrl(url)
            if (response.isSuccessful) {
                val characterResponse = response.body()
                nextPageUrl = characterResponse?.info?.next
                characterResponse?.results.orEmpty()
            } else {
                throw ApiException("Error fetching next page characters")
            }
        } else {
            emptyList()
        }
    }

    suspend fun getCharacterById(id: Int): Character {
        val response = apiService.getCharacterById(id)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw ApiException("Error fetching character details")
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
        val network = connectivityManager?.activeNetwork
        val networkCapabilities = connectivityManager?.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}