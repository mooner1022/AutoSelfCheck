package dev.mooner.autoselfdiagnosis.ui.initial

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.mooner.autoselfdiagnosis.R
import dev.mooner.autoselfdiagnosis.Session
import dev.mooner.autoselfdiagnosis.databinding.ActivityInitialConfigBinding
import dev.mooner.autoselfdiagnosis.enums.Regions
import dev.mooner.autoselfdiagnosis.enums.SchoolKind
import dev.mooner.autoselfdiagnosis.objects.AdvUserInfo
import dev.mooner.autoselfdiagnosis.objects.Config
import dev.mooner.autoselfdiagnosis.objects.SchoolInfo
import dev.mooner.autoselfdiagnosis.ui.main.MainActivity
import dev.mooner.autoselfdiagnosis.ui.steps.AdvInfoFragment
import dev.mooner.autoselfdiagnosis.ui.steps.SetSchoolFragment
import dev.mooner.autoselfdiagnosis.ui.steps.SetTimeFragment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class InitialConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitialConfigBinding
    private var cursor: Int = 0

    companion object {
        lateinit var buttonPrevious: FloatingActionButton
        lateinit var buttonNext: FloatingActionButton
        var isEnd = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.frame_stepper) as NavHostFragment
        val navController = navHostFragment.navController
        setupActionBarWithNavController(
            navController,
            AppBarConfiguration.Builder(
                R.id.step_1_dest,
                R.id.step_2_dest,
                R.id.step_3_dest
            ).build()
        )
        val stepper = binding.stepper
        stepper.setupWithNavController(navController)

        buttonPrevious = binding.buttonPrevious
        binding.buttonPrevious.setOnClickListener {
            stepper.goToPreviousStep()
            cursor--
            updateButton()
        }

        buttonNext = binding.buttonNext
        binding.buttonNext.setOnClickListener {
            if (isEnd) {
                val schoolInfo = Session.stateMap[SetSchoolFragment.KEY_SET_SCHOOL] as SchoolInfo
                val region = Session.stateMap[SetSchoolFragment.KEY_REGION] as Regions
                val kind = Session.stateMap[SetSchoolFragment.KEY_KIND] as SchoolKind
                val advInfo = Session.stateMap[AdvInfoFragment.KEY_ADV_INFO] as AdvUserInfo
                val hour = Session.stateMap[SetTimeFragment.KEY_HOUR] as Int
                val minute = Session.stateMap[SetTimeFragment.KEY_MINUTE] as Int

                val config = Config(
                    region = region,
                    kind = kind,
                    school = schoolInfo,
                    name = advInfo.name,
                    birthday = advInfo.birth,
                    pw = advInfo.password,
                    hour = hour,
                    minute = minute
                )

                val pref = applicationContext.getSharedPreferences("general", 0)
                pref.edit {
                    putString("config", Json.encodeToString(config))
                    putBoolean("isInit", false)
                }

                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("isInitial", true)
                }
                startActivity(intent)
                finish()
            } else {
                stepper.goToNextStep()
                cursor++
                updateButton()
            }
        }
    }

    private fun updateButton() {
        when(cursor) {
            0 -> {
                if (!binding.buttonPrevious.isOrWillBeHidden) {
                    binding.buttonPrevious.hide()
                }
            }
            else -> {
                if (binding.buttonNext.isOrWillBeHidden) {
                    binding.buttonNext.show()
                }
                if (binding.buttonPrevious.isOrWillBeHidden) {
                    binding.buttonPrevious.show()
                }
            }
        }
    }
}