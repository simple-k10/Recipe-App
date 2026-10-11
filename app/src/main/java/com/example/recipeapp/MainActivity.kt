
package com.example.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipeapp.ui.theme.RecipeAppTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip


private val Cream = Color(0xFFFFF8F0)
private val Orange = Color(0xFFE87935)
private val Dark = Color(0xFF29251F)


data class Recipe(
    val name: String,
    val category: String,
    val emoji: String,
    val time: String,
    val description: String,
    val imageRes: Int
)



private val recipes = listOf(
    Recipe(
        "Masala Dosa", "Breakfast", "🥞", "25 min",
        "Crispy South Indian favorite",
        R.drawable.masala_dosa
    ),
    Recipe(
        "Paneer Curry", "Lunch", "🍛", "30 min",
        "Creamy and full of flavor",
        R.drawable.paneer_curry
    ),
    Recipe(
        "Veg Biryani", "Dinner", "🍚", "40 min",
        "Aromatic rice with spices",
        R.drawable.veg_biriyani
    ),
    Recipe(
        "Chocolate Cake", "Dessert", "🍰", "50 min",
        "A sweet homemade treat",
        R.drawable.chocolate_cake
    ),
    Recipe(
        "Fresh Salad", "Healthy", "🥗", "10 min",
        "Fresh and colorful vegetables",
        R.drawable.fresh_salad
    ),
    Recipe(
        "Fruit Smoothie", "Healthy", "🥤", "5 min",
        "A refreshing fruity drink",
        R.drawable.fruit_smoothie
    )
)


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RecipeAppTheme {
                RecipeHome()
            }
        }
    }
}

@Composable
fun RecipeHome() {
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedTab by remember { mutableStateOf("Home") }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }


    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val favoriteRecipes by RecipeStorage
        .getFavorites(context)
        .collectAsState(initial = emptySet())


    val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Healthy", "Dessert")

    val filteredRecipes = recipes.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
                (it.name.contains(search, ignoreCase = true) ||
                        it.category.contains(search, ignoreCase = true))
    }

    val visibleRecipes = if (selectedTab == "Favorites") {
        recipes.filter { it.name in favoriteRecipes }
    } else {
        filteredRecipes
    }


    Scaffold(
        containerColor = Cream,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                listOf("Home", "Favorites", "Profile").forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Text(
                                when (tab) {
                                    "Home" -> "⌂"
                                    "Favorites" -> "♥"
                                    else -> "●"
                                },
                                fontSize = 22.sp
                            )
                        },
                        label = { Text(tab) }
                    )
                }
            }
        }
    ) { padding ->
        if (selectedRecipe != null) {
            RecipeDetails(
                recipe = selectedRecipe!!,
                modifier = Modifier.padding(padding),
                onBack = { selectedRecipe = null }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Text("RECIPE BOOK", color = Orange, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("What would you\nlike to cook today?",
                        fontSize = 29.sp, lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold, color = Dark)
                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⌕  ", fontSize = 23.sp, color = Orange)
                        BasicTextField(
                            value = search,
                            onValueChange = { search = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            decorationBox = { inner ->
                                if (search.isEmpty()) {
                                    Text("Search recipes...", color = Color.Gray)
                                }
                                inner()
                            }
                        )
                    }
                }

                item {
                    Text("Categories", fontSize = 21.sp,
                        fontWeight = FontWeight.Bold, color = Dark)
                    Spacer(Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.chunked(3).forEach { group ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                group.forEach { category ->
                                    val active = selectedCategory == category
                                    Surface(
                                        onClick = { selectedCategory = category },
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (active) Orange else Color.White,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            category,
                                            modifier = Modifier.padding(
                                                horizontal = 4.dp, vertical = 11.dp
                                            ),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            color = if (active) Color.White else Dark,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                repeat(3 - group.size) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        if (selectedTab == "Favorites") "Your Favorites"
                        else if (selectedTab == "Profile") "Explore Recipes"
                        else "Popular Recipes",
                        fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Dark
                    )
                }


                items(visibleRecipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        favorite = recipe.name in favoriteRecipes,
                        onClick = {
                            selectedRecipe = recipe
                        },

                        onFavoriteClick = {
                            scope.launch {
                                val updatedFavorites =
                                    if (recipe.name in favoriteRecipes) {
                                        favoriteRecipes - recipe.name
                                    } else {
                                        favoriteRecipes + recipe.name
                                    }

                                RecipeStorage.saveFavorites(
                                    context,
                                    updatedFavorites
                                )
                            }
                        }

                    )
                }



                if (selectedTab == "Favorites" && visibleRecipes.isEmpty()) {
                    item {
                        Text(
                            "No favorites yet. Tap the ♡ on a recipe to save it.",
                            color = Color.Gray
                        )
                    }
                }


                if (visibleRecipes.isEmpty() && selectedTab != "Favorites") {
                    item { Text("No recipes found. Try another search.") }
                }
            }
        }
    }
}



@Composable
fun RecipeCard(
    recipe: Recipe,
    favorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(86.dp)
                    .background(Cream, RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = recipe.imageRes),
                    contentDescription = recipe.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(recipe.category.uppercase(), color = Orange,
                    fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(recipe.name, fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, color = Dark)
                Spacer(Modifier.height(5.dp))
                Text(recipe.description, fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(7.dp))
                Text("◷  ${recipe.time}", fontSize = 12.sp, color = Orange)
            }
            TextButton(
                onClick = onFavoriteClick,
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(
                    text = if (favorite) "♥" else "♡",
                    color = if (favorite) Orange else Color.Gray,
                    fontSize = 26.sp
                )
            }
        }
    }
}


@Composable
fun RecipeDetails(
    recipe: Recipe,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val ingredients = when (recipe.name) {
        "Masala Dosa" -> listOf(
            "Dosa batter",
            "Potatoes",
            "Onion and green chillies",
            "Mustard seeds and curry leaves",
            "Oil and salt"
        )
        "Paneer Curry" -> listOf(
            "Paneer cubes",
            "Tomatoes and onions",
            "Ginger and garlic",
            "Cooking cream",
            "Indian spices and oil"
        )
        "Veg Biryani" -> listOf(
            "Basmati rice",
            "Mixed vegetables",
            "Onions and tomatoes",
            "Biryani masala",
            "Mint, coriander and oil"
        )
        "Chocolate Cake" -> listOf(
            "Flour",
            "Cocoa powder",
            "Sugar",
            "Milk and oil",
            "Baking powder"
        )
        "Fresh Salad" -> listOf(
            "Cucumber",
            "Tomatoes",
            "Onion",
            "Lemon juice",
            "Salt and pepper"
        )
        else -> listOf(
            "Your favorite fruits",
            "Milk or yogurt",
            "Honey (optional)",
            "Ice cubes (optional)"
        )
    }

    val instructions = when (recipe.name) {
        "Masala Dosa" -> listOf(
            "Prepare the potato masala with onions and spices.",
            "Spread dosa batter thinly on a hot, greased pan.",
            "Cook until crisp, add potato masala and fold.",
            "Serve hot with chutney and sambar."
        )
        "Paneer Curry" -> listOf(
            "Chop the onions and tomatoes.",
            "Cook them with ginger, garlic and spices.",
            "Add paneer and a little water or cream.",
            "Simmer until cooked and serve with rice or roti."
        )
        "Veg Biryani" -> listOf(
            "Wash and partially cook the basmati rice.",
            "Saute vegetables with onions and biryani spices.",
            "Layer the rice and vegetables in a covered pot.",
            "Cook on low heat until the rice is tender."
        )
        "Chocolate Cake" -> listOf(
            "Mix the dry ingredients in a bowl.",
            "Add milk and oil and mix until smooth.",
            "Pour into a prepared cake tin.",
            "Bake in a preheated oven until cooked through."
        )
        "Fresh Salad" -> listOf(
            "Wash all vegetables thoroughly.",
            "Chop the vegetables into bite-sized pieces.",
            "Mix with lemon juice, salt and pepper.",
            "Serve fresh."
        )
        else -> listOf(
            "Wash and prepare your fruits.",
            "Add the fruits and milk to a blender.",
            "Blend until smooth.",
            "Pour into a glass and serve chilled."
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Cream)
            .padding(horizontal = 20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back to recipes", color = Orange)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.White, RoundedCornerShape(24.dp))
                ) {
                    Image(
                        painter = painterResource(id = recipe.imageRes),
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }


                Spacer(Modifier.height(16.dp))

                Text(
                    recipe.name,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "${recipe.category}  •  ${recipe.time}",
                    fontSize = 15.sp,
                    color = Orange
                )

                Spacer(Modifier.height(8.dp))

                Text(recipe.description, fontSize = 15.sp, color = Dark)
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                Text(
                    "Ingredients",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )
            }

            items(ingredients) { ingredient ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✓", color = Orange, fontSize = 18.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(ingredient, fontSize = 15.sp, color = Dark)
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                Text(
                    "Cooking Instructions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )
            }

            items(instructions.size) { index ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Orange, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${index + 1}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        instructions[index],
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        color = Dark,
                        lineHeight = 21.sp
                    )
                }
            }
        }
    }
}

