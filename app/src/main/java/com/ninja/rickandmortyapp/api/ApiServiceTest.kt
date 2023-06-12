package com.ninja.rickandmortyapp.api

import com.ninja.rickandmortyapp.model.Character
import com.ninja.rickandmortyapp.model.CharacterResponse
import com.ninja.rickandmortyapp.model.Info
import com.ninja.rickandmortyapp.model.Location
import com.ninja.rickandmortyapp.model.Origin
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.Response

class ApiServiceTest {
    private val apiService: ApiService = createApiService()

    @Test
    fun testGetCharacters() {
        runBlocking {
            val response = apiService.getCharacters()

            assertEquals(200, response.code())
            assertNotNull(response.body())
            assertNotNull(response.body()?.results)
        }
    }

    @Test
    fun testGetCharactersByUrl() {
        runBlocking {
            val url = "https://rickandmortyapi.com/api/character?page=1"
            val response = apiService.getCharactersByUrl(url)

            assertEquals(200, response.code())
            assertNotNull(response.body())
            assertNotNull(response.body()?.results)
        }
    }

    @Test
    fun testGetCharacterById() {
        runBlocking {
            val id = 1
            val response = apiService.getCharacterById(id)

            assertEquals(200, response.code())
            assertNotNull(response.body())
        }
    }

    private fun createApiService(): ApiService {
        return object : ApiService {
            override suspend fun getCharacters(): Response<CharacterResponse> {
                val character1 = Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    species = "Human",
                    type = "",
                    gender = "Male",
                    origin = Origin("Earth", "https://example.com/earth"),
                    location = Location("Earth", "https://example.com/earth"),
                    image = "https://example.com/rick.jpg",
                    episode = listOf("https://example.com/episode1", "https://example.com/episode2"),
                    url = "https://example.com/rick",
                    created = "2022-01-01"
                )

                val character2 = Character(
                    id = 2,
                    name = "Morty Smith",
                    status = "Alive",
                    species = "Human",
                    type = "",
                    gender = "Male",
                    origin = Origin("Earth", "https://example.com/earth"),
                    location = Location("Earth", "https://example.com/earth"),
                    image = "https://example.com/morty.jpg",
                    episode = listOf("https://example.com/episode1", "https://example.com/episode2"),
                    url = "https://example.com/morty",
                    created = "2022-01-01"
                )

                val characterResponse = CharacterResponse(
                    info = Info(2, 1, null, null),
                    results = listOf(character1, character2)
                )

                return Response.success(characterResponse)
            }

            override suspend fun getCharactersByUrl(url: String): Response<CharacterResponse> {
                return getCharacters()
            }

            override suspend fun getCharacterById(id: Int): Response<Character> {
                val character = Character(
                    id = id,
                    name = "Rick Sanchez",
                    status = "Alive",
                    species = "Human",
                    type = "",
                    gender = "Male",
                    origin = Origin("Earth", "https://example.com/earth"),
                    location = Location("Earth", "https://example.com/earth"),
                    image = "https://example.com/rick.jpg",
                    episode = listOf("https://example.com/episode1", "https://example.com/episode2"),
                    url = "https://example.com/rick",
                    created = "2022-01-01"
                )

                return Response.success(character)
            }
        }
    }
}