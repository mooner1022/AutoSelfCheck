package dev.mooner.autoselfdiagnosis.ui.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import dev.mooner.autoselfdiagnosis.R
import dev.mooner.autoselfdiagnosis.Session
import dev.mooner.autoselfdiagnosis.databinding.FragmentAdvInfoBinding
import dev.mooner.autoselfdiagnosis.objects.AdvUserInfo
import dev.mooner.autoselfdiagnosis.ui.initial.InitialConfigActivity

class AdvInfoFragment : Fragment() {

    companion object {
        const val KEY_ADV_INFO = 3
    }

    private var _binding: FragmentAdvInfoBinding? = null
    private val binding: FragmentAdvInfoBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvInfoBinding.inflate(inflater, container, false)

        var nameFlag = false
        var birthFlag = false
        var pwFlag = false

        fun update() {
            if (nameFlag && birthFlag && pwFlag) {
                InitialConfigActivity.buttonNext.show()
                Session.stateMap[KEY_ADV_INFO] = AdvUserInfo(
                    name = binding.name.text.toString(),
                    birth = binding.birth.text.toString(),
                    password = binding.password.text.toString()
                )
            } else {
                InitialConfigActivity.buttonNext.hide()
            }
        }

        @Suppress("UNCHECKED_CAST")
        if (Session.stateMap.containsKey(KEY_ADV_INFO)) {
            val info = Session.stateMap[KEY_ADV_INFO]!! as AdvUserInfo
            binding.name.setText(info.name)
            binding.birth.setText(info.birth)
            binding.password.setText(info.password)
            InitialConfigActivity.buttonNext.show()
            InitialConfigActivity.buttonNext.setImageResource(R.drawable.ic_arrow_right)
            InitialConfigActivity.isEnd = false
        }

        binding.name.textChanged {
            nameFlag = it.isNotBlank()
            update()
        }
        binding.birth.textChanged {
            birthFlag = it.isNotBlank()
            update()
        }
        binding.password.textChanged {
            pwFlag = it.isNotBlank()
            update()
        }

        return binding.root
    }
}
