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
    
    val nameState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }
    val selectedClassIdState = remember { mutableIntStateOf(initialClassId ?: 1) }
    val notesState = remember { mutableStateOf("") }
    val dateObtainedState = remember { mutableLongStateOf(System.currentTimeMillis()) }
    val isEditModeState = remember { mutableStateOf(false) }

    LaunchedEffect(achievementId) {
        if (achievementId != null && achievementId != -1) {
            val achievement = viewModel.getAchievementById(achievementId)
            achievement?.let {
                nameState.value = it.name
                descriptionState.value = it.description
                selectedClassIdState.intValue = it.classId
                notesState.value = it.notes
                dateObtainedState.longValue = it.dateObtained
                isEditModeState.value = true
            }
        }
    }

    AchievementFormContent(
        classes = classes,
        name = nameState.value,
        onNameChange = { nameState.value = it },
        description = descriptionState.value,
        onDescriptionChange = { descriptionState.value = it },
        selectedClassId = selectedClassIdState.intValue,
        onClassSelected = { selectedClassIdState.intValue = it },
        notes = notesState.value,
        onNotesChange = { notesState.value = it },
        dateObtained = dateObtainedState.longValue,
        onDateChange = { dateObtainedState.longValue = it },
        isEditMode = isEditModeState.value,
        onNavigateBack = onNavigateBack,
        onSave = {
            val achievement = Achievement(
                id = if (isEditModeState.value) achievementId!! else 0,
                classId = selectedClassIdState.intValue,
                name = nameState.value,
                description = descriptionState.value,
                dateObtained = dateObtainedState.longValue,
                notes = notesState.value
            )
            if (isEditModeState.value) viewModel.update(achievement) else viewModel.insert(achievement)
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
    val isErrorState = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateObtained)
    val showDatePickerState = remember { mutableStateOf(false) }

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
                onValueChange = { 
                    onNameChange(it)
                    isErrorState.value = false 
                },
                label = { Text("Achievement Name (Required)") },
                modifier = Modifier.fillMaxWidth(),
                isError = isErrorState.value && name.isBlank()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { 
                    onDescriptionChange(it)
                    isErrorState.value = false 
                },
                label = { Text("Description (Required)") },
                modifier = Modifier.fillMaxWidth(),
                isError = isErrorState.value && description.isBlank()
            )

            Text("Select Class:", style = MaterialTheme.typography.labelLarge)
            val expandedState = remember { mutableStateOf(false) }
            val selectedClassName = classes.find { it.id == selectedClassId }?.name ?: "Select Class"

            ExposedDropdownMenuBox(
                expanded = expandedState.value,
                onExpandedChange = { expandedState.value = it }
            ) {
                OutlinedTextField(
                    value = selectedClassName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Class") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedState.value) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedState.value,
                    onDismissRequest = { expandedState.value = false }
                ) {
                    classes.forEach { tf2Class ->
                        DropdownMenuItem(
                            text = { Text(tf2Class.name) },
                            onClick = {
                                onClassSelected(tf2Class.id)
                                expandedState.value = false
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
                modifier = Modifier.fillMaxWidth().clickable { showDatePickerState.value = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Button(onClick = { showDatePickerState.value = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Change Date")
            }

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Notes (How did you get it?)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (isErrorState.value) {
                Text(
                    text = "Please fill in the Name and Description.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    if (name.isBlank() || description.isBlank()) {
                        isErrorState.value = true
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

    if (showDatePickerState.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerState.value = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateChange(it) }
                    showDatePickerState.value = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerState.value = false }) { Text("Cancel") }
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
            onNameChange = { _ -> },
            description = "",
            onDescriptionChange = { _ -> },
            selectedClassId = 1,
            onClassSelected = { _ -> },
            notes = "",
            onNotesChange = { _ -> },
            dateObtained = System.currentTimeMillis(),
            onDateChange = { _ -> },
            isEditMode = false,
            onNavigateBack = {},
            onSave = {}
        )
    }
}
