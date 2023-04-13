package com.engin.note_app.presentation.note_add_edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.engin.note_app.databinding.FragmentNoteEditBinding
import com.engin.note_app.R
import com.engin.note_app.utils.CustomSnackBar
import com.engin.note_app.utils.Route
import com.engin.note_app.utils.UiEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class NoteAddEditFragment : Fragment(), MenuProvider {

    private var _binding: FragmentNoteEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteAddEditViewModel by viewModels()
    private lateinit var navController: NavController
    private val args by navArgs<NoteAddEditFragmentArgs>()
    private var mode: Int = EDIT_MODE

    private val backPressedDispatcher = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Redirect to our own function
            this@NoteAddEditFragment.onBackPressed()
        }
    }


    companion object {
        const val EDIT_MODE = 0
        const val CREATION_MODE = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeData()
        observeEvent()
    }

    // Setting menu to toolbar for this fragment
    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupUI() {
        // Custom call back added
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedDispatcher
        )
        setMenu()
        navController = findNavController()
        mode = if (args.noteId == 0L) CREATION_MODE else EDIT_MODE
        // Adding custom callback for back press
        binding.apply {
            // Setting screen mode
            editMode = mode == EDIT_MODE
            confirmButton.text =
                if (mode != CREATION_MODE) getString(R.string.save) else getString(R.string.add_note)
            noteImage.visibility = if( mode == CREATION_MODE) View.GONE else View.VISIBLE
            confirmButton.setOnClickListener {
                if (mode == CREATION_MODE)
                    viewModel.createNote(
                        title = binding.noteTitleEt.text.toString(),
                        description = binding.noteDescriptionEt.text.toString(),
                        imageUrl = binding.noteImageEt.text.toString()
                    )
                else
                    viewModel.editNote(
                        title = binding.noteTitleEt.text.toString(),
                        description = binding.noteDescriptionEt.text.toString(),
                        imageUrl = binding.noteImageEt.text.toString(),
                        createdDate = binding.note?.createdDate ?: Date()
                    )
            }
        }
    }

    /**
     *
     * Observing event such as navigating and showing message with Channel
     *
     */
    private fun observeEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackBar -> {
                            CustomSnackBar.make(
                                context = requireContext(),
                                view = requireView(),
                                type = event.snackType,
                                text = event.message,
                                duration = Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is UiEvent.Navigate<*> -> {
                            when (event.route) {
                                Route.Note -> {
                                    // Setting data to SaveStateHandle and trigger refresh
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "shouldRefresh",
                                        true
                                    )
                                    navController.popBackStack()
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * Observing state and update views
     *
     */
    @SuppressLint("CheckResult")
    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { currentState ->
                        if (currentState.isLoading) {
                            binding.isLoading = true
                            return@collect
                        } else {
                            binding.isLoading = false
                        }
                        currentState.note?.let { note ->
                            // Setting node and downloading image
                            binding.note = note
                            if (note.imageUrl.isNotEmpty()) {
                                binding.noteImage.visibility = View.VISIBLE
                                Glide.with(binding.noteImage).load(note.imageUrl)
                                    .let { requestBuilder ->
                                        requestBuilder.error(R.drawable.ic_baseline_image_not_supported)
                                        requestBuilder.into(binding.noteImage)
                                    }
                            }else{
                                binding.noteImage.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_note_edit, menu)
        // For creation mode hiding delete item
        val deleteMenu = menu.findItem(R.id.delte_note_menu)
        deleteMenu.isVisible = mode != CREATION_MODE
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.delte_note_menu -> {
                viewModel.deleteNote()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }


    private fun onBackPressed() {
        // Not trigger refresh
        navController.previousBackStackEntry?.savedStateHandle?.set("shouldRefresh", false)
        navController.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}