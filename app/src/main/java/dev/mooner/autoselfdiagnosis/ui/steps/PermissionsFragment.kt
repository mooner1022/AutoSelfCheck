package dev.mooner.autoselfdiagnosis.ui.steps

import android.Manifest
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.innfinity.permissionflow.lib.requestPermissions
import dev.mooner.autoselfdiagnosis.R
import dev.mooner.autoselfdiagnosis.Session
import dev.mooner.autoselfdiagnosis.databinding.FragmentPermissionsBinding
import dev.mooner.autoselfdiagnosis.ui.initial.InitialConfigActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PermissionsFragment : Fragment() {

    companion object {
        private const val KEY_IS_ALLOWED = -1
    }

    private var _binding: FragmentPermissionsBinding? = null
    private val binding: FragmentPermissionsBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionsBinding.inflate(inflater, container, false)
        val context = requireContext()

        fun nextStep() {
            InitialConfigActivity.buttonNext.show()
            binding.textView.text = "권한 허용 완료! ( ͡° ͜ʖ ͡°)✧"
            binding.buttonAllowPerm.visibility = View.GONE
        }

        if (Session.stateMap.containsKey(KEY_IS_ALLOWED)) {
            nextStep()
        }

        binding.buttonAllowPerm.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                requestPermissions(
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).collect { permissions ->
                    val isAllGranted = permissions.find { !it.isGranted } == null
                    if (isAllGranted) {
                        nextStep()
                        Session.stateMap[KEY_IS_ALLOWED] = true
                    } else {
                        Snackbar.make(it, "모든 권한이 허용되지 않았어요.", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        val imageLoader = ImageLoader.Builder(context)
            .componentRegistry {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder(context))
                } else {
                    add(GifDecoder())
                }
            }
            .build()
        binding.imageView.load(
            drawableResId = R.drawable.gif_check,
            imageLoader = imageLoader
        )

        return binding.root
    }
}