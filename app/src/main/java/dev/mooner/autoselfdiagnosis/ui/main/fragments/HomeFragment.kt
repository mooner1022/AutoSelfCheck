package dev.mooner.autoselfdiagnosis.ui.main.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.snackbar.Snackbar
import dev.mooner.autoselfdiagnosis.*
import dev.mooner.autoselfdiagnosis.databinding.FragmentHomeBinding
import dev.mooner.autoselfdiagnosis.objects.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private lateinit var pref: SharedPreferences
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private var updateTimer: Timer? = null
    private val updateTask: TimerTask
        get() = object: TimerTask() {
            override fun run() {
                val diffMillis = Session.nextAlarmTime - System.currentTimeMillis()
                val formatStr = Utils.formatTime(diffMillis)
                mainScope.launch {
                    binding.uptimeText.setText(formatStr)
                }
            }
        }
    private val serializer: ThreadLocal<Json> = object : ThreadLocal<Json>() {
        override fun initialValue(): Json {
            return Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val context = requireContext()

        pref = context.getSharedPreferences(Const.PREF_NAME, 0)
        val config: Config = serializer.get()!!.decodeFromString(pref.getString("config", "")!!)
        setUpdateTimer()

        binding.uptimeText.setInAnimation(context, R.anim.text_fade_in)
        binding.uptimeText.setOutAnimation(context, R.anim.text_fade_out)
        binding.school.text = config.school.krName
        binding.time.text = "${config.hour}??? ${config.minute}???"

        binding.checkNow.setOnClickListener {
            MaterialDialog(it.context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                cornerRadius(25f)
                title(text = "?????? ???????????? ??????")
                message(text = "?????? ?????? ??????????????? ????????????????")
                positiveButton(text = "??????") { dialog ->
                    dialog.dismiss()
                    CoroutineScope(Dispatchers.Default).launch {
                        SelfChecker(config).check()
                        Snackbar.make(it, "???????????? ??????! (???????????-)???", Snackbar.LENGTH_LONG).show()
                    }
                }
                negativeButton(text = "??????") { dialog ->
                    dialog.dismiss()
                }
            }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        removeUpdateTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeUpdateTimer()
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        setUpdateTimer()
        val config: Config = serializer.get()!!.decodeFromString(pref.getString("config", "")!!)
        binding.time.text = "${config.hour}??? ${config.minute}???"
    }

    private fun setUpdateTimer() {
        if (updateTimer != null) return
        updateTimer = Timer()
        updateTimer!!.schedule(updateTask, 0, 1000)
    }

    private fun removeUpdateTimer() {
        if (updateTimer != null) {
            updateTimer!!.cancel()
            updateTimer!!.purge()
            updateTimer = null
        }
    }
}