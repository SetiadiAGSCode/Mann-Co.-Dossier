package com.setiadi0053.mobpro_asses2.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setiadi0053.mobpro_asses2.data.entity.Achievement
import com.setiadi0053.mobpro_asses2.data.entity.Tf2Class
import com.setiadi0053.mobpro_asses2.ui.AchievementViewModel
import com.setiadi0053.mobpro_asses2.ui.theme.MobProAsses2Theme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AchievementFormScreen(
    viewModel: AchievementViewModel,
    achievementId: Int? = null,
    initialClassId: Int? = null,
    onNavigateBack: () -> Unit
) {
    val classes by viewModel.allClasses.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedClassId by remember { mutableIntStateOf(initialClassId ?: 1) }
    var notes by remember { mutableStateOf("") }
    var dateObtained by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isEditMode by remember { mutableStateOf(false) }

    LaunchedEffect(achievementId) {
        if (achievementId != null && achievementId != -1) {
            val achievement = viewModel.getAchievementById(achievementId)
            achievement?.let {
                name = it.name
                description = it.description
                selectedClassId = it.classId
                notes = it.notes
                dateObtained = it.dateObtained
                isEditMode = true
            }
        }
    }

    AchievementFormContent(
        classes = classes,
        name = name,
        onNameChange = { name = it },
        description = description,
        onDescriptionChange = { description = it },
        selectedClassId = selectedClassId,
        onClassSelected = { selectedClassId = it },
        notes = notes,
        onNotesChange = { notes = it },
        dateObtained = dateObtained,
        onDateChange = { dateObtained = it },
        isEditMode = isEditMode,
        onNavigateBack = onNavigateBack,
        onSave = {
            val achievement = Achievement(
                id = if (isEditMode) achievementId!! else 0,
                classId = selectedClassId,
                name = name,
                description = description,
                dateObtained = dateObtained,
                notes = notes
            )
            if (isEditMode) viewModel.update(achievement) else viewModel.insert(achievement)
            onNavigateBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementFormContent(
    classes: List<Tf2Class>,
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedClassId: Int,
    onClassSelected: (Int) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    dateObtained: Long,
    onDateChange: (Long) -> Unit,
    isEditMode: Boolean,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit
) {
    var isError by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateObtained)
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Achievement" else "Log Achievement") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { onNameChange(it); isError = false },
                label = { Text("Achievement Name (Required)") },
                modifier = Modifier.fillMaxWidth(),
                isError = isError && name.isBlank()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { onDescriptionChange(it); isError = false },
                label = { Text("Description (Required)") },
                modifier = Modifier.fillMaxWidth(),
                isError = isError && description.isBlank()
            )

            Text("Select Class:", style = MaterialTheme.typography.labelLarge)
            var expanded by remember { mutableStateOf(false) }
            val selectedClassName = classes.find { it.id == selectedClassId }?.name ?: "Select Class"

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedClassName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Class") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    classes.forEach { tf2Class ->
                        DropdownMenuItem(
                            text = { Text(tf2Class.name) },
                            onClick = {
                                onClassSelected(tf2Class.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            OutlinedTextField(
                value = dateFormatter.format(Date(dateObtained)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Date Obtained") },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Change Date")
            }

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Notes (How did you get it?)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (isError) {
                Text(
                    text = "Please fill in the Name and Description.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    if (name.isBlank() || description.isBlank()) {
                        isError = true
                    } else {
                        onSave()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Save Changes" else "Log Achievement")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Preview(showBackground = true)
@Composable
fun AchievementFormPreview() {
    MobProAsses2Theme {
        AchievementFormContent(
            classes = listOf(Tf2Class(1, "Scout", "#BD3B3B")),
            name = "",
            onNameChange = {},
            description = "",
            onDescriptionChange = {},
            selectedClassId = 1,
            onClassSelected = {},
            notes = "",
            onNotesChange = {},
            dateObtained = System.currentTimeMillis(),
            onDateChange = {},
            isEditMode = false,
            onNavigateBack = {},
            onSave = {}
        )
    }
}
