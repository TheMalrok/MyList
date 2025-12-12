package com.example.mylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mylist.komponenty.SingleListItem1
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyListApp()
        }
    }
}

@Composable
fun MyListApp() {
    val context = LocalContext.current
    val repository = remember { PersonRepository(context) }
    val navController = rememberNavController()
    val personsList = remember { repository.loadPersons() }

    DisposableEffect(personsList.size) {
        repository.savePersons(personsList)
        onDispose { }
    }

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            ListScreen(
                personsList = personsList,
                repository = repository,
                onPersonClick = { name, description ->
                    val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                    val encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8.toString())
                    navController.navigate("person/$encodedName/$encodedDescription")
                }
            )
        }
        composable(
            route = "person/{name}/{description}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedName = backStackEntry.arguments?.getString("name") ?: ""
            val encodedDescription = backStackEntry.arguments?.getString("description") ?: ""
            val name = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())
            val description = URLDecoder.decode(encodedDescription, StandardCharsets.UTF_8.toString())

            PersonScreen(
                title = name,
                description = description,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun ListScreen(
    personsList: MutableList<Pair<String, String>>,
    repository: PersonRepository,
    onPersonClick: (String, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj osobę"
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            personsList.forEach { person ->
                Modifier.SingleListItem1(
                    title = person.first,
                    description = person.second
                ) { onPersonClick(person.first, person.second) }
            }
        }
    }

    // Dialog do dodawania osoby
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                nameInput = ""
                descriptionInput = ""
            },
            title = { Text("Dodaj nową osobę") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Imię i nazwisko") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        label = { Text("Opis") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            personsList.add(
                                Pair(
                                    nameInput,
                                    descriptionInput.ifBlank { "Brak opisu" }
                                )
                            )
                            repository.savePersons(personsList)
                            showDialog = false
                            nameInput = ""
                            descriptionInput = ""
                        }
                    }
                ) {
                    Text("Dodaj")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        nameInput = ""
                        descriptionInput = ""
                    }
                ) {
                    Text("Anuluj")
                }
            }
        )
    }
}
