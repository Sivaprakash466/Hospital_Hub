package com.example.data.repository

import com.example.data.local.HospitalDao
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.Patient
import com.example.data.model.WardRoom
import kotlinx.coroutines.flow.Flow

class HospitalRepository(private val dao: HospitalDao) {

    // Patients
    val allPatients: Flow<List<Patient>> = dao.getAllPatients()
    val criticalPatients: Flow<List<Patient>> = dao.getCriticalPatients()

    suspend fun getPatientById(id: Long): Patient? = dao.getPatientById(id)

    suspend fun admitPatient(patient: Patient): Long = dao.insertPatient(patient)

    suspend fun updatePatient(patient: Patient) = dao.updatePatient(patient)

    suspend fun dischargePatient(patient: Patient) {
        val updated = patient.copy(status = "Discharged", bedNumber = "N/A")
        dao.updatePatient(updated)
    }

    suspend fun deletePatient(patient: Patient) = dao.deletePatient(patient)

    // Doctors
    val allDoctors: Flow<List<Doctor>> = dao.getAllDoctors()

    suspend fun addDoctor(doctor: Doctor): Long = dao.insertDoctor(doctor)

    suspend fun updateDoctor(doctor: Doctor) = dao.updateDoctor(doctor)

    suspend fun updateDoctorStatus(doctorId: Long, status: String) =
        dao.updateDoctorStatus(doctorId, status)

    suspend fun deleteDoctor(doctor: Doctor) = dao.deleteDoctor(doctor)

    // Appointments
    val allAppointments: Flow<List<Appointment>> = dao.getAllAppointments()

    suspend fun bookAppointment(appointment: Appointment): Long =
        dao.insertAppointment(appointment)

    suspend fun updateAppointment(appointment: Appointment) =
        dao.updateAppointment(appointment)

    suspend fun updateAppointmentStatus(appointmentId: Long, status: String) =
        dao.updateAppointmentStatus(appointmentId, status)

    suspend fun cancelAppointment(appointment: Appointment) =
        dao.updateAppointment(appointment.copy(status = "Cancelled"))

    suspend fun deleteAppointment(appointment: Appointment) =
        dao.deleteAppointment(appointment)

    // Wards
    val allWards: Flow<List<WardRoom>> = dao.getAllWards()

    suspend fun addWard(ward: WardRoom): Long = dao.insertWard(ward)

    suspend fun updateWard(ward: WardRoom) = dao.updateWard(ward)

    suspend fun updateWardOccupiedBeds(wardId: Long, occupied: Int) =
        dao.updateWardOccupiedBeds(wardId, occupied)
}
