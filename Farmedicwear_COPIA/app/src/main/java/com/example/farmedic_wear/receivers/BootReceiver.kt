package com.example.farmedic_wear.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.farmedic_wear.ui.viewmodel.FarmaMedicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val viewModel = FarmaMedicViewModel(context)
            CoroutineScope(Dispatchers.Default).launch {
                viewModel.loadData()
                if (viewModel.hydrationState.remindersEnabled && viewModel.hydrationState.customFrequency != null) {
                    viewModel.scheduleHydrationAlarm()
                }
                viewModel.medications.forEach { med ->
                    if (med.remindersEnabled) {
                        viewModel.scheduleMedicationAlarm(med.id, med.frequencyHours)
                    }
                }
            }
        }
    }
}