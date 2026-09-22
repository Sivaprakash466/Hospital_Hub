package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Patient
import com.example.ui.screens.AdmitPatientDialog
import com.example.ui.screens.AppointmentsScreen
import com.example.ui.screens.BookAppointmentDialog
import com.example.ui.screens.AddDoctorDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DoctorsScreen
import com.example.ui.screens.PatientDetailDialog
import com.example.ui.screens.PatientsScreen
import com.example.ui.screens.WardsScreen
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.viewmodel.HospitalViewModel

enum class HospitalTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Overview", Icons.Default.Dashboard),
    PATIENTS("Patients", Icons.Default.People),
    APPOINTMENTS("Visits", Icons.Default.CalendarMonth),
    DOCTORS("Doctors", Icons.Default.MedicalServices),
    WARDS("Wards", Icons.Default.Hotel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHospitalApp(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(HospitalTab.DASHBOARD) }

    // Dialog visibility states
    var showAdmitDialog by remember { mutableStateOf(false) }
    var showBookAppointmentDialog by remember { mutableStateOf(false) }
    var showAddDoctorDialog by remember { mutableStateOf(false) }
    var selectedPatientForDetail by remember { mutableStateOf<Patient?>(null) }

    // Collect reactive state from ViewModel
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val allPatients by viewModel.allPatients.collectAsStateWithLifecycle()
    val filteredPatients by viewModel.filteredPatients.collectAsStateWithLifecycle()
    val patientSearchQuery by viewModel.patientSearchQuery.collectAsStateWithLifecycle()
    val patientCategory by viewModel.patientFilterCategory.collectAsStateWithLifecycle()

    val allDoctors by viewModel.allDoctors.collectAsStateWithLifecycle()
    val filteredDoctors by viewModel.filteredDoctors.collectAsStateWithLifecycle()
    val doctorDeptFilter by viewModel.doctorDepartmentFilter.collectAsStateWithLifecycle()

    val allAppointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    val filteredAppointments by viewModel.filteredAppointments.collectAsStateWithLifecycle()
    val apptFilter by viewModel.appointmentFilter.collectAsStateWithLifecycle()

    val allWards by viewModel.allWards.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(HospitalTealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Hospital Hub",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Clinical Care Center",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAdmitDialog = true },
                        modifier = Modifier.testTag("topbar_admit_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Admit Patient",
                            tint = HospitalTealPrimary
                        )
                    }
                    IconButton(
                        onClick = { showBookAppointmentDialog = true },
                        modifier = Modifier.testTag("topbar_appointment_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = "Book Appointment",
                            tint = HospitalTealPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.testTag("hospital_bottom_navigation")
            ) {
                HospitalTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    HospitalTab.DASHBOARD -> {
                        val criticalList = allPatients.filter {
                            it.status.equals("Critical", ignoreCase = true) || it.admissionType.equals("ICU", ignoreCase = true)
                        }
                        DashboardScreen(
                            stats = stats,
                            criticalPatients = criticalList,
                            recentPatients = allPatients,
                            upcomingAppointments = allAppointments.filter { it.status == "Scheduled" },
                            onNavigateToPatients = { selectedTab = HospitalTab.PATIENTS },
                            onNavigateToAppointments = { selectedTab = HospitalTab.APPOINTMENTS },
                            onNavigateToDoctors = { selectedTab = HospitalTab.DOCTORS },
                            onNavigateToWards = { selectedTab = HospitalTab.WARDS },
                            onAdmitPatientClick = { showAdmitDialog = true },
                            onBookAppointmentClick = { showBookAppointmentDialog = true },
                            onSelectPatient = { selectedPatientForDetail = it }
                        )
                    }

                    HospitalTab.PATIENTS -> {
                        PatientsScreen(
                            patients = filteredPatients,
                            searchQuery = patientSearchQuery,
                            onSearchQueryChange = { viewModel.setPatientSearchQuery(it) },
                            selectedCategory = patientCategory,
                            onCategorySelected = { viewModel.setPatientFilterCategory(it) },
                            onPatientClick = { selectedPatientForDetail = it },
                            onAdmitPatientClick = { showAdmitDialog = true },
                            onDischargeClick = { viewModel.dischargePatient(it) }
                        )
                    }

                    HospitalTab.APPOINTMENTS -> {
                        AppointmentsScreen(
                            appointments = filteredAppointments,
                            selectedFilter = apptFilter,
                            onFilterSelected = { viewModel.setAppointmentFilter(it) },
                            onBookAppointmentClick = { showBookAppointmentDialog = true },
                            onMarkCompleted = { appt ->
                                viewModel.updateAppointmentStatus(appt.id, "Completed")
                            },
                            onCancelAppointment = { appt ->
                                viewModel.updateAppointmentStatus(appt.id, "Cancelled")
                            }
                        )
                    }

                    HospitalTab.DOCTORS -> {
                        DoctorsScreen(
                            doctors = filteredDoctors,
                            selectedDepartment = doctorDeptFilter,
                            onDepartmentSelected = { viewModel.setDoctorDepartmentFilter(it) },
                            onToggleDuty = { viewModel.toggleDoctorDuty(it) },
                            onAddDoctorClick = { showAddDoctorDialog = true }
                        )
                    }

                    HospitalTab.WARDS -> {
                        WardsScreen(
                            wards = allWards,
                            onUpdateOccupancy = { ward, newCount ->
                                viewModel.updateBedOccupancy(ward.id, newCount)
                            }
                        )
                    }
                }
            }
        }
    }

    // Admit Patient Dialog
    if (showAdmitDialog) {
        AdmitPatientDialog(
            availableDoctors = allDoctors,
            availableWards = allWards,
            onDismiss = { showAdmitDialog = false },
            onAdmit = { newPatient ->
                viewModel.admitPatient(newPatient)
                showAdmitDialog = false
            }
        )
    }

    // Schedule / Book Appointment Dialog
    if (showBookAppointmentDialog) {
        BookAppointmentDialog(
            availableDoctors = allDoctors,
            onDismiss = { showBookAppointmentDialog = false },
            onBook = { appt ->
                viewModel.bookAppointment(appt)
                showBookAppointmentDialog = false
            }
        )
    }

    // Add Doctor Dialog
    if (showAddDoctorDialog) {
        AddDoctorDialog(
            onDismiss = { showAddDoctorDialog = false },
            onAdd = { doctor ->
                viewModel.addDoctor(doctor)
                showAddDoctorDialog = false
            }
        )
    }

    // Patient Detail Dialog
    selectedPatientForDetail?.let { currentPatient ->
        // Retrieve latest version from state
        val latestPatient = allPatients.find { it.id == currentPatient.id } ?: currentPatient
        PatientDetailDialog(
            patient = latestPatient,
            onDismiss = { selectedPatientForDetail = null },
            onUpdateVitals = { bp, pulse, spO2, temp, status, notes ->
                viewModel.updatePatientVitals(latestPatient, bp, pulse, spO2, temp, status, notes)
            },
            onDischarge = { p ->
                viewModel.dischargePatient(p)
                selectedPatientForDetail = null
            }
        )
    }
}
