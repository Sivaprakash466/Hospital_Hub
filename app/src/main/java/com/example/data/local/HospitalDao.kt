package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.Patient
import com.example.data.model.WardRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface HospitalDao {

    // Patients
    @Query("SELECT * FROM patients ORDER BY admissionDate DESC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE id = :id")
    suspend fun getPatientById(id: Long): Patient?

    @Query("SELECT * FROM patients WHERE status = 'Critical' OR admissionType = 'ICU' ORDER BY admissionDate DESC")
    fun getCriticalPatients(): Flow<List<Patient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<Patient>)

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)

    // Doctors
    @Query("SELECT * FROM doctors ORDER BY name ASC")
    fun getAllDoctors(): Flow<List<Doctor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: Doctor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctors(doctors: List<Doctor>)

    @Update
    suspend fun updateDoctor(doctor: Doctor)

    @Delete
    suspend fun deleteDoctor(doctor: Doctor)

    @Query("UPDATE doctors SET status = :status WHERE id = :doctorId")
    suspend fun updateDoctorStatus(doctorId: Long, status: String)

    // Appointments
    @Query("SELECT * FROM appointments ORDER BY appointmentDate ASC, timeSlot ASC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<Appointment>)

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("UPDATE appointments SET status = :status WHERE id = :appointmentId")
    suspend fun updateAppointmentStatus(appointmentId: Long, status: String)

    // Wards
    @Query("SELECT * FROM wards ORDER BY wardName ASC")
    fun getAllWards(): Flow<List<WardRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWard(ward: WardRoom): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWards(wards: List<WardRoom>)

    @Update
    suspend fun updateWard(ward: WardRoom)

    @Query("UPDATE wards SET occupiedBeds = :occupied WHERE id = :wardId")
    suspend fun updateWardOccupiedBeds(wardId: Long, occupied: Int)
}
