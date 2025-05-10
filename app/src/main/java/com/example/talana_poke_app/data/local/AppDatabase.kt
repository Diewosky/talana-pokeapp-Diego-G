package com.example.talana_poke_app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.talana_poke_app.data.local.converter.PokemonTypeConverter
import com.example.talana_poke_app.data.local.dao.FavoritePokemonDao
import com.example.talana_poke_app.data.local.dao.PokemonCacheDao
import com.example.talana_poke_app.data.local.entity.FavoritePokemon
import com.example.talana_poke_app.data.local.entity.PokemonCache

@Database(
    entities = [FavoritePokemon::class, PokemonCache::class], 
    version = 3, 
    exportSchema = false
)
@TypeConverters(PokemonTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritePokemonDao(): FavoritePokemonDao
    abstract fun pokemonCacheDao(): PokemonCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pokemon_database"
                )
                .fallbackToDestructiveMigration() // Para manejar la migración de versión 1 a 2
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 