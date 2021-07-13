package dev.mooner.autoselfdiagnosis.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.gridItems
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.snackbar.Snackbar
import dev.mooner.autoselfdiagnosis.*
import dev.mooner.autoselfdiagnosis.databinding.ActivityMainBinding
import dev.mooner.autoselfdiagnosis.objects.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pref: SharedPreferences
    private val serializer: ThreadLocal<Json> = object : ThreadLocal<Json>() {
        override fun initialValue(): Json {
            return Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        this.supportActionBar?.hide()
        setContentView(binding.root)

        pref = applicationContext.getSharedPreferences(Const.PREF_NAME, 0)

        if (intent.hasExtra("isInitial")) {
            Snackbar.make(binding.root, "초기 설정이 완료되었어요!", Snackbar.LENGTH_LONG).show()
        }
        val config: Config = serializer.get()!!.decodeFromString(pref.getString("config", "")!!)
        AutoCheckTaskManager.setAlarm(applicationContext, config.hour, config.minute)
        binding.userName.text = "${config.name} 님!"

        if (!ForegroundTask.isRunning) {
            val intent = Intent(this, ForegroundTask::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

        binding.viewPager.adapter = ViewPagerAdapter(this)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val id = when(position) {
                    0 -> R.id.home
                    1 -> R.id.settings
                    2 -> R.id.info
                    else -> R.id.name
                }
                binding.bottomBar.menu.select(id)
            }
        })

        val bottomBar = binding.bottomBar
        bottomBar.onItemSelectedListener = { _, item, _ ->
            val index = when(item.id) {
                R.id.home -> 0
                R.id.settings -> 1
                R.id.info -> 2
                else -> 0
            }
            binding.viewPager.setCurrentItem(index, true)
        }
    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.viewPager.currentItem = binding.viewPager.currentItem - 1
        }
    }
}