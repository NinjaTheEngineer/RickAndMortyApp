package com.ninja.rickandmortyapp.api

import com.ninja.rickandmortyapp.model.Character
import com.ninja.rickandmortyapp.model.CharacterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {
    @GET("character")
    suspend fun getCharacters(): Response<CharacterResponse>
    @GET
    suspend fun getCharactersByUrl(@Url url: String): Response<CharacterResponse>

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): Response<Character>
}