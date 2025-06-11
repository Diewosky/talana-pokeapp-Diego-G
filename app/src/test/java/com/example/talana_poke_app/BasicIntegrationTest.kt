package com.example.talana_poke_app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.talana_poke_app.data.model.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BasicIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `data models can be instantiated correctly`() = runTest(testDispatcher) {
        // Given/When - crear instancias de los modelos de datos
        val pokemonListItem = PokemonListItem(
            name = "pikachu", 
            url = "https://pokeapi.co/api/v2/pokemon/25/"
        )
        
        val pokemonSprites = PokemonSprites(
            frontDefault = "https://example.com/pikachu.png"
        )
        
        val pokemonTypeInfo = PokemonTypeInfo(
            name = "electric",
            url = "https://pokeapi.co/api/v2/type/13/"
        )
        
        val pokemonTypeHolder = PokemonTypeHolder(
            slot = 1,
            type = pokemonTypeInfo
        )

        // Then - verificar que se crearon correctamente
        assertThat(pokemonListItem.name).isEqualTo("pikachu")
        assertThat(pokemonListItem.url).contains("pokemon/25")
        
        assertThat(pokemonSprites.frontDefault).contains("pikachu.png")
        
        assertThat(pokemonTypeInfo.name).isEqualTo("electric")
        assertThat(pokemonTypeHolder.slot).isEqualTo(1)
        assertThat(pokemonTypeHolder.type.name).isEqualTo("electric")
    }

    @Test
    fun `PokemonDetailResponse can be created with complete data`() = runTest(testDispatcher) {
        // Given
        val sprites = PokemonSprites("https://example.com/pikachu.png")
        val typeInfo = PokemonTypeInfo("electric", "url")
        val typeHolder = PokemonTypeHolder(1, typeInfo)
        val abilityInfo = PokemonAbilityInfo("static", "url")
        val abilityHolder = PokemonAbilityHolder(abilityInfo, false, 1)
        val statInfo = PokemonStatInfo("hp", "url")
        val statHolder = PokemonStatHolder(55, 0, statInfo)

        // When
        val pokemonDetail = PokemonDetailResponse(
            id = 25,
            name = "pikachu",
            height = 4,
            weight = 60,
            sprites = sprites,
            types = listOf(typeHolder),
            abilities = listOf(abilityHolder),
            stats = listOf(statHolder)
        )

        // Then
        assertThat(pokemonDetail.id).isEqualTo(25)
        assertThat(pokemonDetail.name).isEqualTo("pikachu")
        assertThat(pokemonDetail.height).isEqualTo(4)
        assertThat(pokemonDetail.weight).isEqualTo(60)
        assertThat(pokemonDetail.sprites.frontDefault).contains("pikachu.png")
        assertThat(pokemonDetail.types).hasSize(1)
        assertThat(pokemonDetail.types[0].type.name).isEqualTo("electric")
        assertThat(pokemonDetail.abilities).hasSize(1)
        assertThat(pokemonDetail.abilities[0].ability.name).isEqualTo("static")
        assertThat(pokemonDetail.stats).hasSize(1)
        assertThat(pokemonDetail.stats[0].baseStat).isEqualTo(55)
    }

    @Test
    fun `PokemonListResponse can handle empty and populated lists`() = runTest(testDispatcher) {
        // Given/When - respuesta vacía
        val emptyResponse = PokemonListResponse(results = emptyList())
        
        // Then
        assertThat(emptyResponse.results).isEmpty()
        
        // Given/When - respuesta con datos
        val pokemonList = listOf(
            PokemonListItem("pikachu", "url1"),
            PokemonListItem("charmander", "url2"),
            PokemonListItem("squirtle", "url3")
        )
        val populatedResponse = PokemonListResponse(results = pokemonList)
        
        // Then
        assertThat(populatedResponse.results).hasSize(3)
        assertThat(populatedResponse.results.map { it.name }).containsExactly(
            "pikachu", "charmander", "squirtle"
        )
    }

    @Test
    fun `Pokemon types and abilities are processed correctly`() = runTest(testDispatcher) {
        // Given
        val electricType = PokemonTypeInfo("electric", "type_url")
        val staticAbility = PokemonAbilityInfo("static", "ability_url")
        val hpStat = PokemonStatInfo("hp", "stat_url")

        // When
        val typeHolder = PokemonTypeHolder(1, electricType)
        val abilityHolder = PokemonAbilityHolder(staticAbility, false, 1)
        val statHolder = PokemonStatHolder(55, 0, hpStat)

        // Then
        assertThat(typeHolder.type.name).isEqualTo("electric")
        assertThat(abilityHolder.ability.name).isEqualTo("static")
        assertThat(abilityHolder.isHidden).isFalse()
        assertThat(statHolder.baseStat).isEqualTo(55)
        assertThat(statHolder.stat.name).isEqualTo("hp")
    }

    @Test
    fun `Pokemon data structures support null values appropriately`() = runTest(testDispatcher) {
        // Given/When - sprites con valores nulos
        val spritesWithNull = PokemonSprites(frontDefault = null)
        
        // Then
        assertThat(spritesWithNull.frontDefault).isNull()
        
        // Given/When - detail response con listas vacías
        val minimalDetail = PokemonDetailResponse(
            id = 1,
            name = "test",
            height = 0,
            weight = 0,
            sprites = spritesWithNull,
            types = emptyList(),
            abilities = emptyList(),
            stats = emptyList()
        )
        
        // Then
        assertThat(minimalDetail.types).isEmpty()
        assertThat(minimalDetail.abilities).isEmpty()
        assertThat(minimalDetail.stats).isEmpty()
        assertThat(minimalDetail.sprites.frontDefault).isNull()
    }

    @Test
    fun `Pokemon list processing handles various sizes`() = runTest(testDispatcher) {
        // Given - diferentes tamaños de listas
        val singlePokemon = listOf(PokemonListItem("pikachu", "url"))
        val multiplePokemon = (1..10).map { 
            PokemonListItem("pokemon$it", "url$it") 
        }
        val largePokemon = (1..151).map { 
            PokemonListItem("pokemon$it", "url$it") 
        }

        // When/Then
        assertThat(singlePokemon).hasSize(1)
        assertThat(multiplePokemon).hasSize(10)
        assertThat(largePokemon).hasSize(151)
        
        // Verificar que los nombres se generaron correctamente
        assertThat(multiplePokemon.first().name).isEqualTo("pokemon1")
        assertThat(multiplePokemon.last().name).isEqualTo("pokemon10")
        assertThat(largePokemon.last().name).isEqualTo("pokemon151")
    }
} 