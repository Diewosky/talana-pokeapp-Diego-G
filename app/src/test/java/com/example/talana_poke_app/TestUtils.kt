package com.example.talana_poke_app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * TestRule para configurar el dispatcher principal para tests
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

/**
 * Utilidades comunes para tests
 */
object TestUtils {
    
    /**
     * Crea datos de prueba consistentes para Pokémon
     */
    fun createTestPokemonListItem(name: String, id: Int) = com.example.talana_poke_app.data.model.PokemonListItem(
        name = name,
        url = "https://pokeapi.co/api/v2/pokemon/$id/"
    )
    
    /**
     * Crea respuesta de detalle de Pokémon para tests
     */
    fun createTestPokemonDetailResponse(
        name: String, 
        id: Int,
        types: List<String> = listOf("normal"),
        height: Int = 4,
        weight: Int = 60
    ) = com.example.talana_poke_app.data.model.PokemonDetailResponse(
        id = id,
        name = name,
        height = height,
        weight = weight,
        sprites = com.example.talana_poke_app.data.model.PokemonSprites(
            frontDefault = "https://example.com/$name.png"
        ),
        types = types.mapIndexed { index, typeName ->
            com.example.talana_poke_app.data.model.PokemonTypeHolder(
                slot = index + 1,
                type = com.example.talana_poke_app.data.model.PokemonTypeInfo(
                    name = typeName,
                    url = "https://pokeapi.co/api/v2/type/${index + 1}/"
                )
            )
        },
        abilities = listOf(
            com.example.talana_poke_app.data.model.PokemonAbilityHolder(
                ability = com.example.talana_poke_app.data.model.PokemonAbilityInfo(
                    name = "test-ability",
                    url = "https://pokeapi.co/api/v2/ability/1/"
                ),
                isHidden = false,
                slot = 1
            )
        ),
        stats = listOf(
            com.example.talana_poke_app.data.model.PokemonStatHolder(
                baseStat = 55,
                effort = 0,
                stat = com.example.talana_poke_app.data.model.PokemonStatInfo(
                    name = "hp",
                    url = "https://pokeapi.co/api/v2/stat/1/"
                )
            )
        )
    )
    
    /**
     * Crea un Pokémon favorito para tests
     */
    fun createTestFavoritePokemon(
        userId: String,
        name: String,
        id: Int
    ) = com.example.talana_poke_app.data.local.entity.FavoritePokemon(
        userId = userId,
        name = name,
        detailUrl = "https://pokeapi.co/api/v2/pokemon/$id/",
        imageUrl = "https://example.com/$name.png"
    )
    
    /**
     * Crea un PokemonDisplayItem para tests
     */
    fun createTestPokemonDisplayItem(
        name: String,
        id: Int
    ) = com.example.talana_poke_app.presentation.pokemonlist.PokemonDisplayItem(
        name = name,
        detailUrl = "https://pokeapi.co/api/v2/pokemon/$id/",
        imageUrl = "https://example.com/$name.png"
    )
} 