package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.HospitalDatabase
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.Patient
import com.example.data.model.WardRoom
import com.example.data.repository.HospitalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HospitalDashboardStats(
    val totalPatients: Int = 0,
    val admittedCount: Int = 0,
    val criticalCount: Int = 0,
    val onDutyDoctors: Int = 0,
    val totalDoctors: Int = 0,
    val todayAppointmentsCount: Int = 0,
    val totalBeds: Int = 0,
    val occupiedBeds: Int = 0,
    val availableBeds: Int = 0,
    val occupancyRate: Float = 0f
)

class HospitalViewModel(
    application: Application,
    private val repository: HospitalRepository
) : AndroidViewModel(application) {

    // Filter states
    val patientSearchQuery = MutableStateFlow("")
    val patientFilterCategory = MutableStateFlow("All") // All, Admitted, ICU, Critical, Discharged

    val appointmentFilter = MutableStateFlow("All") // All, Scheduled, Completed, Urgent
    val doctorDepartmentFilter = MutableStateFlow("All") // All, Cardiology, Neurology, etc.

    val allPatients: StateFlow<List<Patient>> = repository.allPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDoctors: StateFlow<List<Doctor>> = repository.allDoctors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAppointments: StateFlow<List<Appointment>> = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWards: StateFlow<List<WardRoom>> = repository.allWards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard aggregated stats
    val dashboardStats: StateFlow<HospitalDashboardStats> = combine(
        allPatients,
        allDoctors,
        allAppointments,
        allWards
    ) { patients, doctors, appointments, wards ->
        val admitted = patients.count { it.status != "Discharged" }
        val critical = patients.count { it.status == "Critical" || it.admissionType == "ICU" }
        val onDuty = doctors.count { it.status == "On Duty" }
        val scheduledAppts = appointments.count { it.status == "Scheduled" }
        val totalBedsCount = wards.sumOf { it.totalBeds }
        val occupiedBedsCount = wards.sumOf { it.occupiedBeds }
        val available = (totalBedsCount - occupiedBedsCount).coerceAtLeast(0)
        val occRate = if (totalBedsCount > 0) occupiedBedsCount.toFloat() / totalBedsCount else 0f

        HospitalDashboardStats(
            totalPatients = patients.size,
            admittedCount = admitted,
            criticalCount = critical,
            onDutyDoctors = onDuty,
            totalDoctors = doctors.size,
            todayAppointmentsCount = scheduledAppts,
            totalBeds = totalBedsCount,
            occupiedBeds = occupiedBedsCount,
            availableBeds = available,
            occupancyRate = occRate
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HospitalDashboardStats())

    // Filtered Patients
    val filteredPatients: StateFlow<List<Patient>> = combine(
        allPatients,
        patientSearchQuery,
        patientFilterCategory
    ) { list, query, filter ->
        list.filter { patient ->
            val matchesQuery = query.isBlank() ||
                patient.name.contains(query, ignoreCase = true) ||
                patient.diagnosis.contains(query, ignoreCase = true) ||
                patient.wardNumber.contains(query, ignoreCase = true) ||
                patient.attendingDoctor.contains(query, ignoreCase = true) ||
                patient.department.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "All" -> true
                "Admitted" -> patient.status != "Discharged"
                "Critical" -> patient.status == "Critical"
                "ICU" -> patient.admissionType == "ICU"
                "Discharged" -> patient.status == "Discharged"
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Appointments
    val filteredAppointments: StateFlow<List<Appointment>> = combine(
        allAppointments,
        appointmentFilter
    ) { list, filter ->
        when (filter) {
            "All" -> list
            "Scheduled" -> list.filter { it.status == "Scheduled" }
            "Completed" -> list.filter { it.status == "Completed" }
            "Urgent" -> list.filter { it.urgency == "Urgent" }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Doctors
    val filteredDoctors: StateFlow<List<Doctor>> = combine(
        allDoctors,
        doctorDepartmentFilter
    ) { list, filter ->
        if (filter == "All") list else list.filter { it.department.equals(filter, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Ensure initial seed if database was created empty
        viewModelScope.launch {
            if (repository.allPatients.stateIn(this).value.isEmpty()) {
                val db = HospitalDatabase.getDatabase(application, viewModelScope)
                HospitalDatabase.populateInitialData(db.hospitalDao())
            }
        }
    }

    // Patient Actions
    fun admitPatient(patient: Patient) {
        viewModelScope.launch {
            repository.admitPatient(patient)
            // If patient is assigned to a ward, increment occupancy
            allWards.value.find { it.wardName.equals(patient.wardNumber, ignoreCase = true) }?.let { ward ->
                if (ward.occupiedBeds < ward.totalBeds) {
                    repository.updateWardOccupiedBeds(ward.id, ward.occupiedBeds + 1)
                }
            }
        }
    }

    fun updatePatient(patient: Patient) {
        viewModelScope.launch {
            repository.updatePatient(patient)
        }
    }

    fun dischargePatient(patient: Patient) {
        viewModelScope.launch {
            repository.dischargePatient(patient)
            // Decrement ward occupancy
            allWards.value.find { it.wardName.equals(patient.wardNumber, ignoreCase = true) }?.let { ward ->
                if (ward.occupiedBeds > 0) {
                    repository.updateWardOccupiedBeds(ward.id, ward.occupiedBeds - 1)
                }
            }
        }
    }

    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            repository.deletePatient(patient)
        }
    }

    fun updatePatientVitals(
        patient: Patient,
        bp: String,
        pulse: Int,
        spO2: Int,
        temp: Double,
        status: String,
        notes: String
    ) {
        viewModelScope.launch {
            val updated = patient.copy(
                vitalsBp = bp,
                vitalsPulse = pulse,
                vitalsSpO2 = spO2,
                vitalsTemp = temp,
                status = status,
                notes = if (notes.isNotBlank()) notes else patient.notes
            )
            repository.updatePatient(updated)
        }
    }

    // Appointment Actions
    fun bookAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.bookAppointment(appointment)
        }
    }

    fun updateAppointmentStatus(appointmentId: Long, status: String) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointmentId, status)
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.deleteAppointment(appointment)
        }
    }

    // Doctor Actions
    fun addDoctor(doctor: Doctor) {
        viewModelScope.launch {
            repository.addDoctor(doctor)
        }
    }

    fun toggleDoctorDuty(doctor: Doctor) {
        val nextStatus = when (doctor.status) {
            "On Duty" -> "In Surgery"
            "In Surgery" -> "Off Duty"
            else -> "On Duty"
        }
        viewModelScope.launch {
            repository.updateDoctorStatus(doctor.id, nextStatus)
        }
    }

    // Ward Actions
    fun updateBedOccupancy(wardId: Long, newOccupiedCount: Int) {
        viewModelScope.launch {
            repository.updateWardOccupiedBeds(wardId, newOccupiedCount)
        }
    }

    fun setPatientSearchQuery(query: String) {
        patientSearchQuery.value = query
    }

    fun setPatientFilterCategory(category: String) {
        patientFilterCategory.value = category
    }

    fun setAppointmentFilter(filter: String) {
        appointmentFilter.value = filter
    }

    fun setDoctorDepartmentFilter(department: String) {
        doctorDepartmentFilter.value = department
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = HospitalDatabase.getDatabase(
                        application,
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
                    )
                    val repository = HospitalRepository(db.hospitalDao())
                    return HospitalViewModel(application, repository) as T
                }
            }
    }
}
