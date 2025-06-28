package com.example.farmedic_wear.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.farmedic_wear.receivers.NotificationReceiver
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json

object LocalDateTimeSerializer : KSerializer<LocalDateTime?> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeString(value.format(formatter))
        }
    }

    override fun deserialize(decoder: Decoder): LocalDateTime? {
        return try {
            val string = decoder.decodeString()
            if (string.isEmpty()) null else LocalDateTime.parse(string, formatter)
        } catch (e: Exception) {
            null
        }
    }
}

@Serializable
data class Medication(
    val id: Int,
    val name: String,
    val dosage: String,
    val times: List<String>,
    val nextDose: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastTaken: LocalDateTime? = null,
    val frequencyHours: Float,
    val remindersEnabled: Boolean = true
)

@Serializable
data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val type: NotificationType,
    val medicationId: Int? = null
)

@Serializable
enum class NotificationType {
    MEDICATION,
    HYDRATION
}

@Serializable
data class HistoryItem(
    val id: Int,
    val type: String,
    val description: String,
    val time: String
)

@Serializable
data class HydrationState(
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastDrunk: LocalDateTime? = null,
    val dailyIntake: Int = 0,
    val goal: Int = 8,
    val customFrequency: Float? = null,
    val remindersEnabled: Boolean = true
)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "farma_medic_prefs")

class FarmaMedicViewModel(private val context: Context) : ViewModel() {

    val activeNotification = MutableStateFlow<Notification?>(null)

    var medications: List<Medication> = emptyList()
    var hydrationState: HydrationState = HydrationState()
    var history: List<HistoryItem> = emptyList()

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    val todayIntake: Int get() = hydrationState.dailyIntake
    val dailyGoal: Int get() = hydrationState.goal

    private val medicationReminderJobs = mutableMapOf<Int, Job>()

    private val MEDICATIONS_KEY = stringPreferencesKey("medications")
    private val HYDRATION_STATE_KEY = stringPreferencesKey("hydration_state")
    private val HISTORY_KEY = stringPreferencesKey("history")

    init {
        viewModelScope.launch {
            try {
                loadData()
                if (hydrationState.remindersEnabled && hydrationState.customFrequency != null) {
                    scheduleHydrationAlarm()
                }
                medications.forEach { medication ->
                    if (medication.remindersEnabled) {
                        scheduleMedicationAlarm(medication.id, medication.frequencyHours)
                    }
                }
            } catch (e: Exception) {
                Log.e("FarmaMedicViewModel", "Error al inicializar: ${e.message}")
                medications = emptyList()
                hydrationState = HydrationState()
                history = emptyList()
            }
        }
    }

    suspend fun loadData() {
        val dataStore = context.dataStore

        try {
            val medicationsJson = dataStore.data.first()[MEDICATIONS_KEY] ?: "[]"
            medications = Json.decodeFromString<List<Medication>>(medicationsJson)
        } catch (e: Exception) {
            Log.e("FarmaMedicViewModel", "Error al cargar medicamentos: ${e.message}")
            medications = emptyList()
        }

        try {
            val hydrationJson = dataStore.data.first()[HYDRATION_STATE_KEY] ?: "{}"
            hydrationState = Json.decodeFromString<HydrationState>(hydrationJson)
        } catch (e: Exception) {
            Log.e("FarmaMedicViewModel", "Error al cargar estado de hidratación: ${e.message}")
            hydrationState = HydrationState()
        }

        try {
            val historyJson = dataStore.data.first()[HISTORY_KEY] ?: "[]"
            history = Json.decodeFromString<List<HistoryItem>>(historyJson)
        } catch (e: Exception) {
            Log.e("FarmaMedicViewModel", "Error al cargar historial: ${e.message}")
            history = emptyList()
        }
    }

    private suspend fun saveData() {
        val dataStore = context.dataStore
        dataStore.edit { preferences ->
            preferences[MEDICATIONS_KEY] = Json.encodeToString(medications)
            preferences[HYDRATION_STATE_KEY] = Json.encodeToString(hydrationState)
            preferences[HISTORY_KEY] = Json.encodeToString(history)
        }
    }

    fun scheduleHydrationAlarm() {
        val intervalMinutes = 15L
        val triggerTime = System.currentTimeMillis() + intervalMinutes * 60 * 1000

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("com.example.farmedic_wear.action.NOTIFICATION_ALARM").apply {
            setClass(context, NotificationReceiver::class.java)
            putExtra(NotificationReceiver.EXTRA_TYPE, NotificationReceiver.TYPE_HYDRATION)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                Log.w("FarmaMedicViewModel", "No se puede programar alarma exacta, permiso denegado.")
                val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelHydrationAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("com.example.farmedic_wear.action.NOTIFICATION_ALARM").apply {
            setClass(context, NotificationReceiver::class.java)
            putExtra(NotificationReceiver.EXTRA_TYPE, NotificationReceiver.TYPE_HYDRATION)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleMedicationAlarm(medicationId: Int, frequencyHours: Float) {
        val intervalSeconds = (frequencyHours * 3600).toLong().coerceAtLeast(60L)
        val triggerTime = System.currentTimeMillis() + intervalSeconds * 1000

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("com.example.farmedic_wear.action.NOTIFICATION_ALARM").apply {
            setClass(context, NotificationReceiver::class.java)
            putExtra(NotificationReceiver.EXTRA_TYPE, NotificationReceiver.TYPE_MEDICATION)
            putExtra(NotificationReceiver.EXTRA_MEDICATION_ID, medicationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                Log.w("FarmaMedicView", "No se puede programar alarma exacta, permiso denegado.")
                val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelMedicationAlarm(medicationId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("com.example.farmedic_wear.action.NOTIFICATION_ALARM").apply {
            setClass(context, NotificationReceiver::class.java)
            putExtra(NotificationReceiver.EXTRA_TYPE, NotificationReceiver.TYPE_MEDICATION)
            putExtra(NotificationReceiver.EXTRA_MEDICATION_ID, medicationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun addMedication(name: String, dosage: String, frequencyHours: Float, remindersEnabled: Boolean) {
        val id = medications.maxOfOrNull { med -> med.id } ?: 0 + 1
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val times = mutableListOf<String>()
        var nextDoseTime = currentTime

        val minutesPerDay = 24 * 60
        val intervalMinutes = (frequencyHours * 60).toInt().coerceAtLeast(1)
        var totalMinutes = 0

        while (totalMinutes < minutesPerDay) {
            times.add(nextDoseTime.format(formatter))
            nextDoseTime = nextDoseTime.plusMinutes(intervalMinutes.toLong())
            totalMinutes += intervalMinutes
        }

        val nextDose = times.first()

        val newMedication = Medication(
            id = id,
            name = name,
            dosage = dosage,
            times = times,
            nextDose = nextDose,
            frequencyHours = frequencyHours,
            remindersEnabled = remindersEnabled
        )

        medications = medications + newMedication
        viewModelScope.launch {
            saveData()
            if (remindersEnabled) {
                scheduleMedicationReminder(id, frequencyHours)
                scheduleMedicationAlarm(id, frequencyHours)
            }
        }
    }

    private fun scheduleMedicationReminder(medicationId: Int, frequencyHours: Float) {
        medicationReminderJobs[medicationId]?.cancel()
        medicationReminderJobs.remove(medicationId)

        val intervalSeconds = (frequencyHours * 3600).toLong().coerceAtLeast(60L)
        if (intervalSeconds < 60L) return

        val job = viewModelScope.launch {
            delay(intervalSeconds * 1000L)
            while (true) {
                val medication = medications.find { med -> med.id == medicationId }
                if (medication == null || !medication.remindersEnabled) break
                triggerMedicationNotification(medicationId)
                delay(intervalSeconds * 1000L)
            }
        }
        medicationReminderJobs[medicationId] = job
    }

    fun updateMedicationRemindersEnabled(medicationId: Int, enabled: Boolean) {
        val medicationIndex = medications.indexOfFirst { med -> med.id == medicationId }
        if (medicationIndex != -1) {
            val medication = medications[medicationIndex]
            medications = medications.toMutableList().apply {
                this[medicationIndex] = medication.copy(remindersEnabled = enabled)
            }
            viewModelScope.launch {
                saveData()
            }
            if (enabled) {
                scheduleMedicationReminder(medicationId, medication.frequencyHours)
                scheduleMedicationAlarm(medicationId, medication.frequencyHours)
            } else {
                medicationReminderJobs[medicationId]?.cancel()
                medicationReminderJobs.remove(medicationId)
                cancelMedicationAlarm(medicationId)
            }
        }
    }

    fun updateHydrationSettings(goal: Int, customFrequency: Float?, remindersEnabled: Boolean) {
        hydrationState = hydrationState.copy(
            goal = goal,
            customFrequency = customFrequency,
            remindersEnabled = remindersEnabled,
            dailyIntake = hydrationState.dailyIntake
        )
        viewModelScope.launch {
            saveData()
            if (remindersEnabled && customFrequency != null) {
                scheduleHydrationReminders()
                scheduleHydrationAlarm()
            } else {
                cancelHydrationAlarm()
            }
        }
    }

    private fun scheduleHydrationReminders() {
        viewModelScope.launch {
            val customFreq = hydrationState.customFrequency
            val intervalMinutes = if (customFreq != null) {
                (customFreq * 60).toLong().coerceAtLeast(1L)
            } else {
                (16f * 60 / hydrationState.goal).toLong().coerceAtLeast(1L)
            }
            delay(intervalMinutes * 60 * 1000L)
            while (hydrationState.remindersEnabled && hydrationState.dailyIntake < hydrationState.goal) {
                triggerMedicationNotification(0)
                delay(intervalMinutes * 60 * 1000L)
            }
        }
    }

    fun dismissNotification() {
        Log.d("FarmaMedicViewModel", "Dismissing notification, setting activeNotification to null")
        activeNotification.value = null
    }

    fun triggerHydrationNotification() {
        val notification = Notification(
            id = System.currentTimeMillis().toInt(),
            title = "¡Es hora de tomar agua!",
            message = "Toma un vaso para mantenerte hidratado",
            type = NotificationType.HYDRATION
        )
        Log.d("FarmaMedicViewModel", "Hydration notification triggered: $notification")
        activeNotification.value = notification
    }

    fun triggerMedicationNotification(medicationId: Int) {
        if (medicationId == 0) {
            triggerHydrationNotification()
            return
        }
        val medication = medications.find { med -> med.id == medicationId }
        medication?.let { med ->
            val notification = Notification(
                id = System.currentTimeMillis().toInt(),
                title = "Hora del medicamento",
                message = "Es hora de tomar ${med.name} ${med.dosage}",
                type = NotificationType.MEDICATION,
                medicationId = medicationId
            )
            Log.d("FarmaMedicViewModel", "Medication notification triggered: $notification")
            activeNotification.value = notification
        } ?: Log.e("FarmaMedicViewModel", "Medication with ID $medicationId not found")
    }

    fun takeMedication(medicationId: Int) {
        val medicationIndex = medications.indexOfFirst { med -> med.id == medicationId }
        if (medicationIndex != -1) {
            val medication = medications[medicationIndex]
            val now = LocalDateTime.now()

            medications = medications.toMutableList().apply {
                this[medicationIndex] = medication.copy(lastTaken = now)
            }

            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val newHistoryItem = HistoryItem(
                id = history.size + 1,
                type = "Medicamento",
                description = "${medication.name} ${medication.dosage} tomada",
                time = currentTime
            )
            history = listOf(newHistoryItem) + history
            viewModelScope.launch {
                saveData()
            }
        }
        dismissNotification()
    }

    fun skipMedication(medicationId: Int) {
        val medication = medications.find { med -> med.id == medicationId }
        medication?.let { med ->
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val newHistoryItem = HistoryItem(
                id = history.size + 1,
                type = "Medicamento",
                description = "${med.name} ${med.dosage} omitida",
                time = currentTime
            )
            history = listOf(newHistoryItem) + history
            viewModelScope.launch {
                saveData()
            }
        }
        dismissNotification()
    }

    fun snoozeMedication(medicationId: Int, minutes: Int) {
        viewModelScope.launch {
            delay(minutes * 60 * 1000L)
            triggerMedicationNotification(medicationId)
        }
        dismissNotification()
    }

    fun drinkWater() {
        hydrationState = hydrationState.copy(
            lastDrunk = LocalDateTime.now(),
            dailyIntake = hydrationState.dailyIntake + 1
        )

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newHistoryItem = HistoryItem(
            id = history.size + 1,
            type = "Hidratación",
            description = "250ml de agua",
            time = currentTime
        )
        history = listOf(newHistoryItem) + history
        viewModelScope.launch {
            saveData()
        }
        dismissNotification()
    }

    fun snoozeHydration(minutes: Int) {
        viewModelScope.launch {
            delay(minutes * 60 * 1000L)
            triggerHydrationNotification()
        }
        dismissNotification()
    }

    fun getMedicationById(id: Int): Medication? {
        return medications.find { med -> med.id == id }
    }

    fun getTimeSinceLastTaken(lastTaken: LocalDateTime?): String {
        if (lastTaken == null) return "No tomado hoy"

        val now = LocalDateTime.now()
        val duration = java.time.Duration.between(lastTaken, now)

        return when {
            duration.toMinutes() < 5 -> "Recién tomado"
            duration.toMinutes() < 60 -> "Hace ${duration.toMinutes()} min"
            duration.toHours() < 24 -> "Hace ${duration.toHours()}h ${duration.toMinutes() % 60}min"
            else -> "Hace ${duration.toDays()} días"
        }
    }

    fun getFormattedTime(dateTime: LocalDateTime?): String {
        return dateTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "N/A"
    }

    fun updateStepCount(steps: Int) {
        _stepCount.value = steps
        Log.d("FarmaMedicViewModel", "Step count updated to $steps")
    }

    override fun onCleared() {
        super.onCleared()
        medicationReminderJobs.values.forEach { it.cancel() }
        medicationReminderJobs.clear()
    }

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FarmaMedicViewModel(context) as T
            }
        }
    }
}