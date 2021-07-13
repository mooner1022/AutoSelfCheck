package dev.mooner.autoselfdiagnosis.ui.steps

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import com.google.android.material.snackbar.Snackbar
import dev.mooner.autoselfdiagnosis.R
import dev.mooner.autoselfdiagnosis.SchoolFinder
import dev.mooner.autoselfdiagnosis.Session
import dev.mooner.autoselfdiagnosis.databinding.FragmentSetSchoolBinding
import dev.mooner.autoselfdiagnosis.enums.Regions
import dev.mooner.autoselfdiagnosis.enums.SchoolKind
import dev.mooner.autoselfdiagnosis.objects.SchoolInfo
import dev.mooner.autoselfdiagnosis.ui.initial.InitialConfigActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SetSchoolFragment : Fragment() {

    companion object {
        const val KEY_SET_SCHOOL = 0
        const val KEY_REGION = 1
        const val KEY_KIND = 2
    }

    private var _binding: FragmentSetSchoolBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageLoader: ImageLoader
    private var loadingDialog: MaterialDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetSchoolBinding.inflate(inflater, container, false)
        val context = requireContext()
        imageLoader = ImageLoader.Builder(context)
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder(context))
                } else {
                    add(GifDecoder())
                }
            }
            .build()
        val finder = SchoolFinder(debug = true)

        val regions = Regions.values()
        binding.region.attachDataSource(regions.map { it.krName })

        val kinds = SchoolKind.values()
        binding.kind.attachDataSource(kinds.map { it.krName })

        fun setSchool(name: String) {
            binding.layoutSearchSchool.visibility = View.GONE
            binding.buttonSearchSchool.visibility = View.GONE
            binding.layoutResult.visibility = View.VISIBLE
            binding.selectedSchool.text = name
            InitialConfigActivity.buttonNext.show()
        }

        if (Session.stateMap.containsKey(KEY_SET_SCHOOL)) {
            val school = Session.stateMap[KEY_SET_SCHOOL]!! as SchoolInfo
            setSchool(school.krName)
        } else {
            InitialConfigActivity.buttonNext.hide()
        }

        binding.buttonSearchSchool.setOnClickListener {
            val region = regions[binding.region.selectedIndex]
            val kind = kinds[binding.kind.selectedIndex]
            val name = binding.schoolName.text.toString()

            CoroutineScope(Dispatchers.Default).launch {
                showLoading()
                val queryResult = finder.find(name, region, kind)
                hideLoading()
                if (queryResult.schoolList.isNullOrEmpty()) {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(it, "조건에 맞는 학교를 찾을 수 없습니다.", Snackbar.LENGTH_LONG).show()
                    }
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        cornerRadius(25f)
                        listItems(items = queryResult.schoolList.map { school -> school.krName }) { _, index, _ ->
                            val school = queryResult.schoolList[index]
                            Session.stateMap.apply {
                                put(KEY_SET_SCHOOL, school)
                                put(KEY_REGION, region)
                                put(KEY_KIND, kind)
                            }

                            setSchool(school.krName)
                        }
                    }
                }
            }
        }

        binding.buttonResetSchool.setOnClickListener {
            binding.layoutSearchSchool.visibility = View.VISIBLE
            binding.buttonSearchSchool.visibility = View.VISIBLE
            binding.layoutResult.visibility = View.GONE
        }

        return binding.root
    }

    private suspend fun showLoading() = withContext(Dispatchers.Main) {
        loadingDialog =  MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
        loadingDialog!!.show {
            cornerRadius(25f)
            customView(R.layout.dialog_loading)

            val animView: ImageView = findViewById(R.id.loadingAnimView)
            animView.load(
                drawableResId = R.drawable.git_search,
                imageLoader = imageLoader
            )
        }
    }

    private suspend fun hideLoading() = withContext(Dispatchers.Main) {
        if (loadingDialog != null) {
            loadingDialog!!.dismiss()
        }
    }
}