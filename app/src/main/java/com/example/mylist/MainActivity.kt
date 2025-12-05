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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mylist.komponenty.SingleListItem1

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val personsList =  remember {
                mutableStateListOf(
                    Pair("Jan Kowalski", "Opis osoby 1"),
                )
            }

            var showDialog by remember { mutableStateOf(false) }
            var nameInput by remember { mutableStateOf("") }
            var descriptionInput by remember { mutableStateOf("") }
            var selectedPerson by remember { mutableStateOf<Pair<String, String>?>(null) }

            if (selectedPerson != null) {
                PersonScreen(
                    title = selectedPerson!!.first,
                    description = selectedPerson!!.second,
                    onBack = { selectedPerson = null }
                )
            } else {
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
                            SingleListItem1(
                                title = person.first,
                                description = person.second,
                                onClick = { selectedPerson = person }
                            )
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
                        //content
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
        }
    }
}
