package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.Patient
import com.example.data.model.WardRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Patient::class, Doctor::class, Appointment::class, WardRoom::class],
    version = 1,
    exportSchema = false
)
abstract class HospitalDatabase : RoomDatabase() {

    abstract fun hospitalDao(): HospitalDao

    companion object {
        @Volatile
        private var INSTANCE: HospitalDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HospitalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HospitalDatabase::class.java,
                    "hospital_hub_database"
                )
                    .addCallback(HospitalDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class HospitalDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.hospitalDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: HospitalDao) {
            val doctors = listOf(
                Doctor(
                    name = "Dr. Marcus Vance",
                    specialty = "Interventional Cardiologist",
                    department = "Cardiology",
                    qualification = "MD, FACC",
                    roomNumber = "Cabin 302",
                    phone = "+1 (555) 234-5678",
                    status = "On Duty",
                    experienceYears = 16,
                    activePatientsCount = 12
                ),
                Doctor(
                    name = "Dr. Elena Rostova",
                    specialty = "Neurosurgeon & Stroke Specialist",
                    department = "Neurology",
                    qualification = "MD, PhD",
                    roomNumber = "Cabin 415",
                    phone = "+1 (555) 345-6789",
                    status = "In Surgery",
                    experienceYears = 14,
                    activePatientsCount = 8
                ),
                Doctor(
                    name = "Dr. James Wilson",
                    specialty = "Internal Medicine & Diagnostics",
                    department = "General Medicine",
                    qualification = "MD, FACP",
                    roomNumber = "Cabin 104",
                    phone = "+1 (555) 456-7890",
                    status = "On Duty",
                    experienceYears = 20,
                    activePatientsCount = 18
                ),
                Doctor(
                    name = "Dr. Priya Sharma",
                    specialty = "Pediatric Critical Care",
                    department = "Pediatrics",
                    qualification = "MD, DCH",
                    roomNumber = "Cabin 208",
                    phone = "+1 (555) 567-8901",
                    status = "On Duty",
                    experienceYears = 11,
                    activePatientsCount = 9
                ),
                Doctor(
                    name = "Dr. David Chen",
                    specialty = "Orthopedic & Trauma Surgeon",
                    department = "Orthopedics",
                    qualification = "MS (Ortho), FAAOS",
                    roomNumber = "Cabin 219",
                    phone = "+1 (555) 678-9012",
                    status = "Off Duty",
                    experienceYears = 15,
                    activePatientsCount = 6
                ),
                Doctor(
                    name = "Dr. Sarah Mitchell",
                    specialty = "Emergency Medicine Director",
                    department = "Emergency",
                    qualification = "MD, FACEP",
                    roomNumber = "ER Trauma Bay 1",
                    phone = "+1 (555) 789-0123",
                    status = "On Duty",
                    experienceYears = 12,
                    activePatientsCount = 14
                )
            )
            dao.insertDoctors(doctors)

            val wards = listOf(
                WardRoom(
                    wardName = "Intensive Care Unit (ICU)",
                    department = "Emergency & Critical Care",
                    floor = "Floor 2 - East Wing",
                    totalBeds = 14,
                    occupiedBeds = 11,
                    nurseInCharge = "Sr. Nurse Clara Higgins"
                ),
                WardRoom(
                    wardName = "Cardiac Care Unit (CCU)",
                    department = "Cardiology",
                    floor = "Floor 3 - North Wing",
                    totalBeds = 16,
                    occupiedBeds = 12,
                    nurseInCharge = "Sr. Nurse Rachel Adams"
                ),
                WardRoom(
                    wardName = "General Male Ward",
                    department = "General Medicine",
                    floor = "Floor 1 - West Wing",
                    totalBeds = 30,
                    occupiedBeds = 23,
                    nurseInCharge = "Nurse Matthew Brooks"
                ),
                WardRoom(
                    wardName = "General Female Ward",
                    department = "General Medicine",
                    floor = "Floor 1 - South Wing",
                    totalBeds = 30,
                    occupiedBeds = 24,
                    nurseInCharge = "Nurse Emily Foster"
                ),
                WardRoom(
                    wardName = "Pediatric Care Ward",
                    department = "Pediatrics",
                    floor = "Floor 2 - Sunshine Wing",
                    totalBeds = 20,
                    occupiedBeds = 14,
                    nurseInCharge = "Sr. Nurse Hannah Abbott"
                ),
                WardRoom(
                    wardName = "Orthopedic Surgical Suites",
                    department = "Orthopedics",
                    floor = "Floor 4 - Executive Wing",
                    totalBeds = 12,
                    occupiedBeds = 7,
                    nurseInCharge = "Nurse Benjamin Clark"
                )
            )
            dao.insertWards(wards)

            val now = System.currentTimeMillis()
            val hourMillis = 3600000L
            val dayMillis = 86400000L

            val patients = listOf(
                Patient(
                    name = "Arthur Pendelton",
                    age = 58,
                    gender = "Male",
                    bloodGroup = "A-",
                    phone = "+1 (555) 111-2233",
                    admissionType = "ICU",
                    department = "Emergency",
                    attendingDoctor = "Dr. Sarah Mitchell",
                    wardNumber = "Intensive Care Unit (ICU)",
                    bedNumber = "Bed 01",
                    diagnosis = "Hypertensive Emergency with Acute Troponin Rise",
                    vitalsBp = "176/112",
                    vitalsPulse = 104,
                    vitalsSpO2 = 93,
                    vitalsTemp = 99.1,
                    status = "Critical",
                    admissionDate = now - (2 * hourMillis),
                    notes = "Continuous telemetry attached. Administering IV labetalol infusion. Hourly arterial blood gas checks required."
                ),
                Patient(
                    name = "Liam O'Connor",
                    age = 7,
                    gender = "Male",
                    bloodGroup = "B+",
                    phone = "+1 (555) 222-3344",
                    admissionType = "Inpatient",
                    department = "Pediatrics",
                    attendingDoctor = "Dr. Priya Sharma",
                    wardNumber = "Pediatric Care Ward",
                    bedNumber = "Bed 07",
                    diagnosis = "Acute Bronchiolitis with High Wheeze",
                    vitalsBp = "102/64",
                    vitalsPulse = 118,
                    vitalsSpO2 = 94,
                    vitalsTemp = 101.4,
                    status = "Critical",
                    admissionDate = now - (5 * hourMillis),
                    notes = "Salbutamol nebulization every 4 hours. Low-flow O2 via nasal cannula at 2L/min."
                ),
                Patient(
                    name = "Robert Jenkins",
                    age = 64,
                    gender = "Male",
                    bloodGroup = "O+",
                    phone = "+1 (555) 333-4455",
                    admissionType = "Inpatient",
                    department = "Cardiology",
                    attendingDoctor = "Dr. Marcus Vance",
                    wardNumber = "Cardiac Care Unit (CCU)",
                    bedNumber = "Bed 04",
                    diagnosis = "Post Percutaneous Coronary Intervention (PCI)",
                    vitalsBp = "128/82",
                    vitalsPulse = 72,
                    vitalsSpO2 = 98,
                    vitalsTemp = 98.6,
                    status = "Stable",
                    admissionDate = now - (1 * dayMillis),
                    notes = "Groin puncture site intact without hematoma. Dual antiplatelet therapy maintained."
                ),
                Patient(
                    name = "Maya Lin",
                    age = 31,
                    gender = "Female",
                    bloodGroup = "A+",
                    phone = "+1 (555) 444-5566",
                    admissionType = "Inpatient",
                    department = "Neurology",
                    attendingDoctor = "Dr. Elena Rostova",
                    wardNumber = "Intensive Care Unit (ICU)",
                    bedNumber = "Bed 03",
                    diagnosis = "Intracranial Pressure Observation Post-Concussion",
                    vitalsBp = "118/74",
                    vitalsPulse = 68,
                    vitalsSpO2 = 99,
                    vitalsTemp = 98.4,
                    status = "Recovering",
                    admissionDate = now - (18 * hourMillis),
                    notes = "Glasgow Coma Scale 15/15. Pupils equal and reactive. MRI brain clear of acute bleed."
                ),
                Patient(
                    name = "Samuel Thorne",
                    age = 73,
                    gender = "Male",
                    bloodGroup = "AB+",
                    phone = "+1 (555) 555-6677",
                    admissionType = "Inpatient",
                    department = "General Medicine",
                    attendingDoctor = "Dr. James Wilson",
                    wardNumber = "General Male Ward",
                    bedNumber = "Bed 14",
                    diagnosis = "Bilateral Lobar Pneumonia",
                    vitalsBp = "124/80",
                    vitalsPulse = 80,
                    vitalsSpO2 = 96,
                    vitalsTemp = 99.4,
                    status = "Stable",
                    admissionDate = now - (2 * dayMillis),
                    notes = "IV Ceftriaxone day 3. Productive cough diminishing. Chest physio scheduled daily."
                ),
                Patient(
                    name = "Aisha Patel",
                    age = 44,
                    gender = "Female",
                    bloodGroup = "O-",
                    phone = "+1 (555) 666-7788",
                    admissionType = "Inpatient",
                    department = "Orthopedics",
                    attendingDoctor = "Dr. David Chen",
                    wardNumber = "General Female Ward",
                    bedNumber = "Bed 09",
                    diagnosis = "Closed Bimalleolar Ankle Fracture - ORIF Post-Op",
                    vitalsBp = "120/78",
                    vitalsPulse = 74,
                    vitalsSpO2 = 98,
                    vitalsTemp = 98.6,
                    status = "Recovering",
                    admissionDate = now - (30 * hourMillis),
                    notes = "Circulation in toes brisk. Pain adequately controlled with oral analgesics. Mobilize with walker."
                ),
                Patient(
                    name = "Clara Oswald",
                    age = 35,
                    gender = "Female",
                    bloodGroup = "B+",
                    phone = "+1 (555) 777-8899",
                    admissionType = "Outpatient",
                    department = "General Medicine",
                    attendingDoctor = "Dr. James Wilson",
                    wardNumber = "Outpatient Clinic A",
                    bedNumber = "N/A",
                    diagnosis = "Routine Diabetes Mellitus Type 2 Follow-up",
                    vitalsBp = "122/76",
                    vitalsPulse = 70,
                    vitalsSpO2 = 99,
                    vitalsTemp = 98.5,
                    status = "Discharged",
                    admissionDate = now - (3 * dayMillis),
                    notes = "HbA1c 6.8%. Metformin 500mg BD continued. Dietician consultation recommended."
                )
            )
            dao.insertPatients(patients)

            val appointments = listOf(
                Appointment(
                    patientName = "Jonathan Davis",
                    patientPhone = "+1 (555) 888-9900",
                    doctorName = "Dr. Marcus Vance",
                    department = "Cardiology",
                    appointmentDate = now + (2 * hourMillis),
                    timeSlot = "09:30 AM",
                    reason = "Post-Stent 3-Month Follow-Up & ECG Evaluation",
                    urgency = "Normal",
                    status = "Scheduled"
                ),
                Appointment(
                    patientName = "Beatrice Ramos",
                    patientPhone = "+1 (555) 999-0011",
                    doctorName = "Dr. Priya Sharma",
                    department = "Pediatrics",
                    appointmentDate = now + (3 * hourMillis),
                    timeSlot = "10:15 AM",
                    reason = "High Spiking Fever & Refusal to Feed (Infant)",
                    urgency = "Urgent",
                    status = "Scheduled"
                ),
                Appointment(
                    patientName = "Kenneth Cole",
                    patientPhone = "+1 (555) 123-4567",
                    doctorName = "Dr. Elena Rostova",
                    department = "Neurology",
                    appointmentDate = now + (5 * hourMillis),
                    timeSlot = "11:30 AM",
                    reason = "Cervical Radiculopathy & Nerve Conduction Review",
                    urgency = "Follow-up",
                    status = "Scheduled"
                ),
                Appointment(
                    patientName = "Nancy Drew",
                    patientPhone = "+1 (555) 234-5678",
                    doctorName = "Dr. James Wilson",
                    department = "General Medicine",
                    appointmentDate = now - hourMillis,
                    timeSlot = "08:30 AM",
                    reason = "Seasonal Allergic Rhinitis & Bronchospasm",
                    urgency = "Normal",
                    status = "Completed"
                ),
                Appointment(
                    patientName = "Lucas Gray",
                    patientPhone = "+1 (555) 345-6789",
                    doctorName = "Dr. David Chen",
                    department = "Orthopedics",
                    appointmentDate = now + (7 * hourMillis),
                    timeSlot = "02:45 PM",
                    reason = "Wrist Plaster Removal & Physiotherapy Referral",
                    urgency = "Normal",
                    status = "Scheduled"
                )
            )
            dao.insertAppointments(appointments)
        }
    }
}
