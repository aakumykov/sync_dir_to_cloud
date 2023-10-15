package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskEdit2Binding
import java.lang.reflect.Modifier

class TaskEditFragment2 : Fragment() {

    private var _binding: FragmentTaskEdit2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskEdit2Binding.inflate(inflater, container, false)

        val view = binding.root

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    MainFrameContent(requireContext())
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun MainFrameContent(context: Context) {
    Row (modifier = Modifier.fillMaxWidth(1f)) {
        Column {
            /*BasicTextField(
                value = "source path",
//                placeholder = { Text("source path") },
                onValueChange = {}
            )*/
            Image(
                imageVector = Icons.Filled.Place,
                contentDescription = stringResource(R.string.description_sync_task_select_source_path)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL)
@Composable
fun Preview() {
    MainFrameContent(context = LocalContext.current)
}