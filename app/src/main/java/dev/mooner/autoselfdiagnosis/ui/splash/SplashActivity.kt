package dev.mooner.autoselfdiagnosis.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.mooner.autoselfdiagnosis.Const
import dev.mooner.autoselfdiagnosis.R
import dev.mooner.autoselfdiagnosis.SchoolFinder
import dev.mooner.autoselfdiagnosis.SelfChecker
import dev.mooner.autoselfdiagnosis.databinding.ActivitySplashBinding
import dev.mooner.autoselfdiagnosis.enums.Regions
import dev.mooner.autoselfdiagnosis.enums.SchoolKind
import dev.mooner.autoselfdiagnosis.objects.Config
import dev.mooner.autoselfdiagnosis.ui.initial.InitialConfigActivity
import dev.mooner.autoselfdiagnosis.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        this.supportActionBar?.hide()
        setContentView(binding.root)

        val pref = applicationContext.getSharedPreferences(Const.PREF_NAME, 0)
        Timer().schedule(1500) {
            if (pref.getBoolean("isInit", true)) {
                startActivity(Intent(this@SplashActivity, InitialConfigActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
        }
    }
}