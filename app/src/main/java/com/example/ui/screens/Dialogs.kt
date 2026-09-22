package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.Patient
import com.example.data.model.WardRoom
import com.example.ui.components.PatientVitalsGrid
import com.example.ui.components.StatusBadge
import com.example.ui.theme.HospitalCriticalRed
import com.example.ui.theme.HospitalCriticalRedContainer
import com.example.ui.theme.HospitalTealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdmitPatientDialog(
    availableDoctors: List<Doctor>,
    availableWards: List<WardRoom>,
    onDismiss: () -> Unit,
    onAdmit: (Patient) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var phone by remember { mutableStateOf("") }
    var admissionType by remember { mutableStateOf("Inpatient") }
    var department by remember { mutableStateOf("General Medicine") }
    var attendingDoctor by remember {
        mutableStateOf(availableDoctors.firstOrNull()?.name ?: "Dr. James Wilson")
    }
    var wardNumber by remember {
        mutableStateOf(availableWards.firstOrNull()?.wardName ?: "General Male Ward")
    }
    var bedNumber by remember { mutableStateOf("Bed 01") }
    var diagnosis by remember { mutableStateOf("") }
    var vitalsBp by remember { mutableStateOf("120/80") }
    var vitalsPulseStr by remember { mutableStateOf("72") }
    var vitalsSpO2Str by remember { mutableStateOf("98") }
    var vitalsTempStr by remember { mutableStateOf("98.6") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val bloodGroups = listOf("A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-")
    val admissionTypes = listOf("Inpatient", "ICU", "Emergency", "Outpatient")
    val departments = listOf(
        "General Medicine",
        "Cardiology",
        "Neurology",
        "Pediatrics",
        "Orthopedics",
        "Emergency"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
                .testTag("admit_patient_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HospitalTealPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = HospitalTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Admit New Patient",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Register patient and allocate bed/ward",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                errorMessage?.let { msg ->
                    Surface(
                        color = HospitalCriticalRedContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = msg,
                            color = HospitalCriticalRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Patient Info Section
                Text(
                    text = "Personal Information",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HospitalTealPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Patient Full Name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admit_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Age *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admit_age_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Phone") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gender Selection
                Text("Gender", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    listOf("Male", "Female", "Other").forEach { g ->
                        FilterChip(
                            selected = gender == g,
                            onClick = { gender = g },
                            label = { Text(g) }
                        )
                    }
                }

                // Blood Group Selection
                Text("Blood Group", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    bloodGroups.forEach { bg ->
                        FilterChip(
                            selected = bloodGroup == bg,
                            onClick = { bloodGroup = bg },
                            label = { Text(bg) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Admission & Ward Section
                Text(
                    text = "Clinical Admission & Allocation",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HospitalTealPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text("Admission Type", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    admissionTypes.forEach { type ->
                        FilterChip(
                            selected = admissionType == type,
                            onClick = { admissionType = type },
                            label = { Text(type) }
                        )
                    }
                }

                // Department
                var deptExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = deptExpanded,
                    onExpandedChange = { deptExpanded = !deptExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = department,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Department") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = deptExpanded,
                        onDismissRequest = { deptExpanded = false }
                    ) {
                        departments.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    department = d
                                    deptExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Attending Doctor
                var docExpanded by remember { mutableStateOf(false) }
                val docOptions = if (availableDoctors.isNotEmpty()) {
                    availableDoctors.map { "${it.name} (${it.specialty})" }
                } else {
                    listOf("Dr. James Wilson (Internal Medicine)", "Dr. Marcus Vance (Cardiologist)")
                }

                ExposedDropdownMenuBox(
                    expanded = docExpanded,
                    onExpandedChange = { docExpanded = !docExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = attendingDoctor,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Attending Doctor") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = docExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = docExpanded,
                        onDismissRequest = { docExpanded = false }
                    ) {
                        availableDoctors.forEach { doc ->
                            DropdownMenuItem(
                                text = { Text("${doc.name} - ${doc.department}") },
                                onClick = {
                                    attendingDoctor = doc.name
                                    docExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = wardNumber,
                        onValueChange = { wardNumber = it },
                        label = { Text("Ward Name") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = bedNumber,
                        onValueChange = { bedNumber = it },
                        label = { Text("Bed No.") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("Primary Diagnosis / Chief Complaint *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admit_diagnosis_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Vitals Section
                Text(
                    text = "Initial Baseline Vitals",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HospitalTealPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = vitalsBp,
                        onValueChange = { vitalsBp = it },
                        label = { Text("BP") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = vitalsPulseStr,
                        onValueChange = { vitalsPulseStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Pulse") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = vitalsSpO2Str,
                        onValueChange = { vitalsSpO2Str = it.filter { c -> c.isDigit() } },
                        label = { Text("SpO2 %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = vitalsTempStr,
                        onValueChange = { vitalsTempStr = it },
                        label = { Text("Temp °F") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Clinical Notes / Known Allergies") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Please enter patient name."
                                return@Button
                            }
                            val age = ageStr.toIntOrNull()
                            if (age == null || age <= 0) {
                                errorMessage = "Please enter a valid age."
                                return@Button
                            }
                            if (diagnosis.isBlank()) {
                                errorMessage = "Please provide primary diagnosis."
                                return@Button
                            }
                            val pulse = vitalsPulseStr.toIntOrNull() ?: 75
                            val spO2 = vitalsSpO2Str.toIntOrNull() ?: 98
                            val temp = vitalsTempStr.toDoubleOrNull() ?: 98.6

                            val initialStatus = if (admissionType == "ICU") "Critical" else "Admitted"

                            val newPatient = Patient(
                                name = name.trim(),
                                age = age,
                                gender = gender,
                                bloodGroup = bloodGroup,
                                phone = if (phone.isBlank()) "+1 (555) 000-0000" else phone.trim(),
                                admissionType = admissionType,
                                department = department,
                                attendingDoctor = attendingDoctor,
                                wardNumber = wardNumber.trim(),
                                bedNumber = bedNumber.trim(),
                                diagnosis = diagnosis.trim(),
                                vitalsBp = vitalsBp.trim(),
                                vitalsPulse = pulse,
                                vitalsSpO2 = spO2,
                                vitalsTemp = temp,
                                status = initialStatus,
                                notes = notes.trim()
                            )
                            onAdmit(newPatient)
                        },
                        modifier = Modifier.testTag("submit_admit_button")
                    ) {
                        Text("Confirm Admission")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentDialog(
    availableDoctors: List<Doctor>,
    onDismiss: () -> Unit,
    onBook: (Appointment) -> Unit
) {
    var patientName by remember { mutableStateOf("") }
    var patientPhone by remember { mutableStateOf("") }
    var selectedDoctor by remember {
        mutableStateOf(availableDoctors.firstOrNull()?.name ?: "Dr. James Wilson")
    }
    var department by remember {
        mutableStateOf(availableDoctors.firstOrNull()?.department ?: "General Medicine")
    }
    var timeSlot by remember { mutableStateOf("10:00 AM") }
    var reason by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("Normal") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val timeSlots = listOf("09:00 AM", "09:45 AM", "10:30 AM", "11:15 AM", "01:30 PM", "02:15 PM", "03:45 PM", "04:30 PM")
    val urgencies = listOf("Normal", "Urgent", "Follow-up")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 620.dp)
                .testTag("book_appointment_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedule Appointment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                errorMessage?.let {
                    Surface(
                        color = HospitalCriticalRedContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = it,
                            color = HospitalCriticalRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("appt_patient_name"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = patientPhone,
                    onValueChange = { patientPhone = it },
                    label = { Text("Patient Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Doctor dropdown
                var docExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = docExpanded,
                    onExpandedChange = { docExpanded = !docExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "$selectedDoctor ($department)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Consulting Doctor *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = docExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = docExpanded,
                        onDismissRequest = { docExpanded = false }
                    ) {
                        availableDoctors.forEach { doc ->
                            DropdownMenuItem(
                                text = { Text("${doc.name} - ${doc.department} (${doc.specialty})") },
                                onClick = {
                                    selectedDoctor = doc.name
                                    department = doc.department
                                    docExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Available Slot", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    timeSlots.take(4).forEach { slot ->
                        FilterChip(
                            selected = timeSlot == slot,
                            onClick = { timeSlot = slot },
                            label = { Text(slot, fontSize = 11.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    timeSlots.drop(4).forEach { slot ->
                        FilterChip(
                            selected = timeSlot == slot,
                            onClick = { timeSlot = slot },
                            label = { Text(slot, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Priority / Urgency", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    urgencies.forEach { urg ->
                        FilterChip(
                            selected = urgency == urg,
                            onClick = { urgency = urg },
                            label = { Text(urg) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Consultation *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (patientName.isBlank()) {
                                errorMessage = "Please enter patient name."
                                return@Button
                            }
                            if (reason.isBlank()) {
                                errorMessage = "Please enter reason for consultation."
                                return@Button
                            }
                            val appt = Appointment(
                                patientName = patientName.trim(),
                                patientPhone = if (patientPhone.isBlank()) "+1 (555) 000-0000" else patientPhone.trim(),
                                doctorName = selectedDoctor,
                                department = department,
                                timeSlot = timeSlot,
                                reason = reason.trim(),
                                urgency = urgency,
                                status = "Scheduled"
                            )
                            onBook(appt)
                        },
                        modifier = Modifier.testTag("submit_appointment_button")
                    ) {
                        Text("Schedule Appointment")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDoctorDialog(
    onDismiss: () -> Unit,
    onAdd: (Doctor) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Cardiology") }
    var specialty by remember { mutableStateOf("") }
    var qualification by remember { mutableStateOf("MD") }
    var roomNumber by remember { mutableStateOf("Cabin 101") }
    var phone by remember { mutableStateOf("") }
    var experienceStr by remember { mutableStateOf("10") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val departments = listOf(
        "Cardiology",
        "Neurology",
        "General Medicine",
        "Pediatrics",
        "Orthopedics",
        "Emergency"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.testTag("add_doctor_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Medical Specialist",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                errorMessage?.let {
                    Surface(
                        color = HospitalCriticalRedContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Text(it, color = HospitalCriticalRed, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Doctor Name (e.g. Dr. John Smith) *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("doctor_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                var deptExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = deptExpanded,
                    onExpandedChange = { deptExpanded = !deptExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = department,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Department") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = deptExpanded,
                        onDismissRequest = { deptExpanded = false }
                    ) {
                        departments.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    department = d
                                    deptExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = { Text("Sub-Specialty (e.g. Interventionalist) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = qualification,
                        onValueChange = { qualification = it },
                        label = { Text("Degree") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { roomNumber = it },
                        label = { Text("Cabin / Room") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Phone") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = experienceStr,
                        onValueChange = { experienceStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Exp (Yrs)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Please enter doctor's name."
                                return@Button
                            }
                            if (specialty.isBlank()) {
                                errorMessage = "Please enter doctor's specialty."
                                return@Button
                            }
                            val doctor = Doctor(
                                name = if (name.startsWith("Dr.")) name.trim() else "Dr. ${name.trim()}",
                                specialty = specialty.trim(),
                                department = department,
                                qualification = qualification.trim(),
                                roomNumber = roomNumber.trim(),
                                phone = if (phone.isBlank()) "+1 (555) 000-0000" else phone.trim(),
                                status = "On Duty",
                                experienceYears = experienceStr.toIntOrNull() ?: 8,
                                activePatientsCount = 0
                            )
                            onAdd(doctor)
                        },
                        modifier = Modifier.testTag("submit_doctor_button")
                    ) {
                        Text("Add Doctor")
                    }
                }
            }
        }
    }
}

@Composable
fun PatientDetailDialog(
    patient: Patient,
    onDismiss: () -> Unit,
    onUpdateVitals: (String, Int, Int, Double, String, String) -> Unit,
    onDischarge: (Patient) -> Unit
) {
    var isEditingVitals by remember { mutableStateOf(false) }
    var editBp by remember { mutableStateOf(patient.vitalsBp) }
    var editPulseStr by remember { mutableStateOf(patient.vitalsPulse.toString()) }
    var editSpO2Str by remember { mutableStateOf(patient.vitalsSpO2.toString()) }
    var editTempStr by remember { mutableStateOf(patient.vitalsTemp.toString()) }
    var editStatus by remember { mutableStateOf(patient.status) }
    var editNotes by remember { mutableStateOf(patient.notes) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }
    val admissionDateFormatted = remember(patient.admissionDate) {
        dateFormat.format(Date(patient.admissionDate))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
                .testTag("patient_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with name and status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = patient.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(status = patient.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${patient.gender} • ${patient.age} yrs • Blood Group ${patient.bloodGroup} • ID #${patient.id}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Location & Doctor Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "WARD / BED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${patient.wardNumber} • ${patient.bedNumber}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "DEPARTMENT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = patient.department,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "ATTENDING PHYSICIAN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = patient.attendingDoctor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HospitalTealPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "ADMITTED ON",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = admissionDateFormatted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Diagnosis
                Text(
                    text = "Clinical Diagnosis",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = patient.diagnosis,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vitals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Vitals",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { isEditingVitals = !isEditingVitals }) {
                        Text(if (isEditingVitals) "Cancel" else "Update Vitals")
                    }
                }

                if (!isEditingVitals) {
                    PatientVitalsGrid(
                        bp = patient.vitalsBp,
                        pulse = patient.vitalsPulse,
                        spO2 = patient.vitalsSpO2,
                        temp = patient.vitalsTemp
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = editBp,
                                onValueChange = { editBp = it },
                                label = { Text("BP") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = editPulseStr,
                                onValueChange = { editPulseStr = it.filter { c -> c.isDigit() } },
                                label = { Text("Pulse") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = editSpO2Str,
                                onValueChange = { editSpO2Str = it.filter { c -> c.isDigit() } },
                                label = { Text("SpO2 %") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = editTempStr,
                                onValueChange = { editTempStr = it },
                                label = { Text("Temp") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Patient Status", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Critical", "Admitted", "Stable", "Recovering").forEach { st ->
                                FilterChip(
                                    selected = editStatus == st,
                                    onClick = { editStatus = st },
                                    label = { Text(st, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val pulse = editPulseStr.toIntOrNull() ?: patient.vitalsPulse
                                val spO2 = editSpO2Str.toIntOrNull() ?: patient.vitalsSpO2
                                val temp = editTempStr.toDoubleOrNull() ?: patient.vitalsTemp
                                onUpdateVitals(editBp, pulse, spO2, temp, editStatus, editNotes)
                                isEditingVitals = false
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Save Vitals")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clinical Notes
                Text(
                    text = "Physician Notes & Instructions",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (patient.notes.isNotBlank()) patient.notes else "No specific notes entered yet.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions: Discharge Patient or Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (patient.status != "Discharged") {
                        OutlinedButton(
                            onClick = {
                                onDischarge(patient)
                                onDismiss()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = HospitalCriticalRed),
                            modifier = Modifier.testTag("discharge_patient_button")
                        ) {
                            Text("Discharge Patient")
                        }
                    } else {
                        Text(
                            text = "Patient Discharged",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(onClick = onDismiss) {
                        Text("Close Record")
                    }
                }
            }
        }
    }
}
