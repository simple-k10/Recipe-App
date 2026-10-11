
package com.example.recipeapp

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.recipeDataStore by preferencesDataStore(
    name = "recipe_preferences"
)

object RecipeStorage {

    private val FAVORITES_KEY =
        stringSetPreferencesKey("favorite_recipes")

    fun getFavorites(context: Context): Flow<Set<String>> {
        return context.recipeDataStore.data.map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }
    }

    suspend fun saveFavorites(
        context: Context,
        favorites: Set<String>
    ) {
        context.recipeDataStore.edit { preferences ->
            preferences[FAVORITES_KEY] = favorites
        }
    }
}
