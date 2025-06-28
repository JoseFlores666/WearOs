package com.example.farmedic_wear.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.farmedic_wear.R
import com.example.farmedic_wear.presentation.MainActivity
import com.example.farmedic_wear.ui.viewmodel.FarmaMedicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "farma_medic_channel"
        const val NOTIFICATION_CHANNEL_NAME = "FarmaMedic Reminders"
        const val EXTRA_TYPE = "notification_type"
        const val EXTRA_MEDICATION_ID = "medication_id"
        const val TYPE_HYDRATION = "hydration"
        const val TYPE_MEDICATION = "medication"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)

        val type = intent.getStringExtra(EXTRA_TYPE) ?: return
        when (type) {
            TYPE_HYDRATION -> {
                showHydrationNotification(context)
                val viewModel = FarmaMedicViewModel(context)
                coroutineScope.launch {
                    viewModel.loadData()
                    if (viewModel.hydrationState.remindersEnabled && viewModel.hydrationState.customFrequency != null) {
                        viewModel.scheduleHydrationAlarm()
                    }
                }
            }
            TYPE_MEDICATION -> {
                val medicationId = intent.getIntExtra(EXTRA_MEDICATION_ID, -1)
                if (medicationId != -1) {
                    showMedicationNotification(context, medicationId)
                    val viewModel = FarmaMedicViewModel(context)
                    coroutineScope.launch {
                        viewModel.loadData()
                        val medication = viewModel.getMedicationById(medicationId)
                        if (medication != null && medication.remindersEnabled) {
                            viewModel.scheduleMedicationAlarm(medicationId, medication.frequencyHours)
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for FarmaMedic reminders"
            setShowBadge(true)
            enableLights(true)
            enableVibration(true)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showHydrationNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_TYPE, TYPE_HYDRATION)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("¡Es hora de tomar agua!")
            .setContentText("Toma un vaso para mantenerte hidratado")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 500))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun showMedicationNotification(context: Context, medicationId: Int) {
        val viewModel = FarmaMedicViewModel(context)
        coroutineScope.launch {
            viewModel.loadData()
            val medication = viewModel.getMedicationById(medicationId) ?: return@launch

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(EXTRA_TYPE, TYPE_MEDICATION)
                putExtra(EXTRA_MEDICATION_ID, medicationId)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                medicationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Hora del medicamento")
                .setContentText("Es hora de tomar ${medication.name} ${medication.dosage}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 500))
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(medicationId, notification)
        }
    }
}