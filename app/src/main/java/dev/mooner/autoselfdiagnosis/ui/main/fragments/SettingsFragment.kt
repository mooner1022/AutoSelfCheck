package dev.mooner.autoselfdiagnosis.ui.main.fragments

import android.os.Bundle
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.snackbar.Snackbar
import com.maxkeppeler.sheets.time_clock.ClockTimeSheet
import dev.mooner.autoselfdiagnosis.AutoCheckTaskManager
import dev.mooner.autoselfdiagnosis.Const
import dev.mooner.autoselfdiagnosis.R
import dev.mooner.autoselfdiagnosis.SelfChecker
import dev.mooner.autoselfdiagnosis.objects.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val context = requireContext()

        val generalPower = findPreference<SwitchPreferenceCompat>("general_onoff")!!
        generalPower.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                if (AutoCheckTaskManager.isAlarmSet(context)) {
                    val config: Config = Json.decodeFromString(context.getSharedPreferences(Const.PREF_NAME, 0).getString("config", "")!!)
                    AutoCheckTaskManager.setAlarm(requireContext(), config.hour, config.minute)
                }
            } else {
                AutoCheckTaskManager.cancelAlarm(context)
            }
            true
        }

        val resetTime = findPreference<Preference>("change_time")!!
        resetTime.setOnPreferenceClickListener {
            ClockTimeSheet().show(context) {
                title("???????????? ??????")
                onPositive { _: Long, hours: Int, minutes: Int ->
                    val pref = context.getSharedPreferences(Const.PREF_NAME, 0)
                    val config: Config = Json.decodeFromString(pref.getString("config", "")!!)
                    config.hour = hours
                    config.minute = minutes
                    pref.edit {
                        putString("config", Json.encodeToString(config))
                    }
                    AutoCheckTaskManager.apply {
                        if (isAlarmSet(context)) {
                            cancelAlarm(context)
                            println("isSet")
                        }
                        setAlarm(context, config.hour, config.minute)
                    }
                    //Snackbar.make(requireView(), "????????? ?????????????????????. ?????? ?????????????????????.", Snackbar.LENGTH_LONG).show()
                }
            }
            true
        }

        val resetAll = findPreference<Preference>("clear_data")!!
        resetAll.setOnPreferenceClickListener {
            MaterialDialog(it.context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                cornerRadius(25f)
                title(text = "?????? ????????? ??????")
                message(text = "?????? ???????????? ???????????? ?????? ????????????????\n??????: ??? ????????? ????????? ??? ????????????.")
                positiveButton(text = "??????") { dialog ->
                    dialog.dismiss()
                    val pref = context.getSharedPreferences(Const.PREF_NAME, 0)
                    pref.edit(commit = true) {
                        clear()
                    }
                    exitProcess(-1)
                }
                negativeButton(text = "??????") { dialog ->
                    dialog.dismiss()
                }
            }
            true
        }
    }
}