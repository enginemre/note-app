package com.engin.note_app.presentation.note_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.engin.note_app.MainActivity
import com.engin.note_app.R
import com.engin.note_app.databinding.FragmentNoteListBinding
import com.engin.note_app.presentation.note_list.adapter.NoteListRecyclerAdapter
import com.engin.note_app.utils.CustomSnackBar
import com.engin.note_app.utils.UiEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NoteListFragment : Fragment() , NoteListListener {


    private  var _binding  : FragmentNoteListBinding? = null
    private  val binding get() = _binding!!

    private  val viewModel : NoteListViewModel by viewModels()
    private  lateinit var adapter: NoteListRecyclerAdapter
    private var navController: NavController? =null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListBinding.inflate(inflater,container,false)
        return  binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeData()
        observeEvent()
    }


    /**
     *
     * Observing event such as navigating and showing message with Channel
     *
     */
    private fun observeEvent() {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiEvent.collect{event->
                    when(event){
                        is UiEvent.ShowSnackBar -> {
                            CustomSnackBar.make(
                                context = requireContext(),
                                view = requireView(),
                                type = event.snackType,
                                text = event.message
                            ).show()
                        }
                        else-> {}
                    }
                }
            }
        }
    }

    // Setting up ui component
    private fun setupUI(){
        findNavController()?.let {
            // Setting nav controller
            navController =it
        }

        // Listening refresh trigger
        navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("shouldRefresh")?.observe(
            viewLifecycleOwner) { result ->
            if(result)
                viewModel.getAllNote()
        }
        // Setting adapter
        adapter = NoteListRecyclerAdapter(
            list = emptyList(), this
        )
        // Adapter listener for empty view
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkEmpty()
            }

            fun checkEmpty() {
                binding.emptyView.visibility = (if (adapter.itemCount == 0) View.VISIBLE else View.GONE)

            }
        })
        // Setting click listeners
        binding.apply {
            recyclerView.adapter = adapter
            addNoteFab.setOnClickListener {
                onAddNote()
            }
        }


    }

    /**
     *
     * Observing state and update views
     *
     */
    private fun observeData(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect{ currentState ->
                    if(currentState.isLoading){
                        binding.isLoading = true
                        return@collect
                    }
                    else{
                        binding.isLoading = false
                    }
                    currentState.noteList?.let {notes->
                        adapter.updateData(notes)
                    }
                }
            }
        }

    }



    /**
     *
     * Passing note id to edit note screen
     *
     */
    override fun onClickNote(id: Long) {
        val action = NoteListFragmentDirections.actionNoteListFragmentToNoteEditFragment(id)
        val navCont = requireView().findNavController()
        navCont.navigate(action)
    }

    /**
     *
     * Navigating with 0L it means that creating new Notes
     *
     */
    override fun onAddNote() {
        val action = NoteListFragmentDirections.actionNoteListFragmentToNoteEditFragment(0L)
        val navCont = requireView().findNavController()
        navCont.navigate(action)
//        findNavController()?.navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findNavController():NavController? {
        val navHostFragment = (requireActivity() as? MainActivity)?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        return navHostFragment?.navController
    }

}