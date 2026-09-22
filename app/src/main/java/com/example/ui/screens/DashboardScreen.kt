package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appointment
import com.example.data.model.Patient
import com.example.ui.components.MetricStatCard
import com.example.ui.components.PatientVitalsGrid
import com.example.ui.components.StatusBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.theme.HospitalCriticalRed
import com.example.ui.theme.HospitalCriticalRedContainer
import com.example.ui.theme.HospitalInfoBlue
import com.example.ui.theme.HospitalSuccessGreen
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.HospitalWarningOrange
import com.example.ui.viewmodel.HospitalDashboardStats

@Composable
fun DashboardScreen(
    stats: HospitalDashboardStats,
    criticalPatients: List<Patient>,
    recentPatients: List<Patient>,
    upcomingAppointments: List<Appointment>,
    onNavigateToPatients: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToDoctors: () -> Unit,
    onNavigateToWards: () -> Unit,
    onAdmitPatientClick: () -> Unit,
    onBookAppointmentClick: () -> Unit,
    onSelectPatient: (Patient) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hospital Banner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(HospitalTealPrimary, Color(0xFF0F3460))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Hospital Hub Operations",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Live Central Clinical & Inpatient Dashboard",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34D399))
                                    )
                                    Text(
                                        text = "LIVE SHIFT",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bed Capacity Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Hospital Bed Occupancy",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${stats.occupiedBeds}/${stats.totalBeds} Beds (${(stats.occupancyRate * 100).toInt()}%)",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { stats.occupancyRate.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (stats.occupancyRate > 0.85f) HospitalWarningOrange else Color(0xFF38BDF8),
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )
                    }
                }
            }
        }

        // Metrics Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Admitted Patients",
                        value = "${stats.admittedCount}",
                        subtitle = "${stats.criticalCount} in Critical/ICU",
                        icon = Icons.Default.People,
                        accentColor = HospitalInfoBlue,
                        onClick = onNavigateToPatients,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Bed Availability",
                        value = "${stats.availableBeds}",
                        subtitle = "Out of ${stats.totalBeds} total",
                        icon = Icons.Default.Hotel,
                        accentColor = HospitalSuccessGreen,
                        onClick = onNavigateToWards,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Doctors On Duty",
                        value = "${stats.onDutyDoctors}/${stats.totalDoctors}",
                        subtitle = "Across all wards",
                        icon = Icons.Default.MedicalServices,
                        accentColor = HospitalTealPrimary,
                        onClick = onNavigateToDoctors,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Appointments",
                        value = "${stats.todayAppointmentsCount}",
                        subtitle = "Scheduled today",
                        icon = Icons.Default.CalendarMonth,
                        accentColor = Color(0xFF8B5CF6),
                        onClick = onNavigateToAppointments,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAdmitPatientClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_admit_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Admit Patient", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onBookAppointmentClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_book_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Critical / High Attention Alert Section (if any)
        if (criticalPatients.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HospitalCriticalRedContainer.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Critical Patients Alert",
                                tint = HospitalCriticalRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "High-Risk ICU & Critical Monitoring (${criticalPatients.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HospitalCriticalRed
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        criticalPatients.take(2).forEach { p ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelectPatient(p) },
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = p.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${p.wardNumber} • ${p.bedNumber} | ${p.diagnosis}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    StatusBadge(status = p.status)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Inpatients Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Inpatients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToPatients) {
                    Text("View All (${stats.totalPatients})")
                }
            }
        }

        items(recentPatients.take(4)) { patient ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectPatient(patient) }
                    .testTag("patient_card_${patient.id}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = patient.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${patient.gender}, ${patient.age} yrs • Blood ${patient.bloodGroup}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(status = patient.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Diagnosis: ${patient.diagnosis}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PatientVitalsGrid(
                        bp = patient.vitalsBp,
                        pulse = patient.vitalsPulse,
                        spO2 = patient.vitalsSpO2,
                        temp = patient.vitalsTemp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍 ${patient.wardNumber} • ${patient.bedNumber}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "🩺 ${patient.attendingDoctor}",
                            fontSize = 11.sp,
                            color = HospitalTealPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Upcoming Appointments Section
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Consultations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToAppointments) {
                    Text("Manage")
                }
            }
        }

        items(upcomingAppointments.take(3)) { appt ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = appt.patientName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            UrgencyBadge(urgency = appt.urgency)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${appt.doctorName} • ${appt.department}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = appt.reason,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HospitalTealPrimary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = appt.timeSlot,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HospitalTealPrimary
                        )
                    }
                }
            }
        }
    }
}
