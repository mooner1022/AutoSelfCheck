package dev.mooner.autoselfdiagnosis.ui.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.maxkeppeler.sheets.time_clock.ClockTimeSheet
import dev.mooner.autoselfdiagnosis.R
import dev.mooner.autoselfdiagnosis.Session
import dev.mooner.autoselfdiagnosis.Utils.Companion.addZero
import dev.mooner.autoselfdiagnosis.databinding.FragmentSetTimeBinding
import dev.mooner.autoselfdiagnosis.ui.initial.InitialConfigActivity

class SetTimeFragment : Fragment() {

    companion object {
        private const val DEF_HOUR = 8
        private const val DEF_MINUTE = 0
        const val KEY_HOUR = 4
        const val KEY_MINUTE = 5
    }

    private var _binding: FragmentSetTimeBinding? = null
    private val binding: FragmentSetTimeBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetTimeBinding.inflate(inflater, container, false)
        val context = requireContext()

        InitialConfigActivity.buttonNext.setImageResource(R.drawable.ic_round_check_24)
        InitialConfigActivity.isEnd = true

        binding.selectedTime.text = format(DEF_HOUR, DEF_MINUTE)
        putTime(DEF_HOUR, DEF_MINUTE)

        binding.buttonResetTime.setOnClickListener {
            ClockTimeSheet().show(context) {
                title("자가진단 시간")
                onPositive { _: Long, hours: Int, minutes: Int ->
                    binding.selectedTime.text = format(hours, minutes)
                    putTime(hours, minutes)
                }
            }
        }

        return binding.root
    }

    private fun format(hour: Int, minute: Int): String {
        return "자가진단 시간\n${hour.addZero()}시 ${minute.addZero()}분"
    }

    private fun putTime(hour: Int, minute: Int) {
        Session.stateMap[KEY_HOUR] = hour
        Session.stateMap[KEY_MINUTE] = minute
    }
}