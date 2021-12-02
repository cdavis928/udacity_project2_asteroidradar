package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.database.AsteroidFilters
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.fragment_main.*


private const val TAG = "MainFragment"

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }

        ViewModelProvider(
            this,
            MainViewModel.Factory(activity.application)
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.mainViewModel = viewModel

        //Create the adapter and its clickable functions
        val adapter = AsteroidAdapter(AsteroidAdapter.AsteroidListener { asteroid ->
            viewModel.onAsteroidClicked(asteroid)
        })
        binding.asteroidRecycler.adapter = adapter

        //Observe the asteroidLiveData to populate the adapter
        // Don't delete or your list will disappear from UI!
        viewModel.asteroidList.observe(viewLifecycleOwner, Observer<List<Asteroid>> {
            it?.let {
                adapter.submitList(it)
            }
        })

        //navigate to the detail fragment
        viewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner, Observer { asteroid ->
            asteroid?.let {
                this.findNavController().navigate(
                    MainFragmentDirections
                        .actionShowDetail(asteroid)
                )
                viewModel.onAsteroidDetailNavigated()
            }
        })
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onToggleFilter(
            when (item.itemId) {
                R.id.show_today_asteroids_menu -> {
                    AsteroidFilters.TODAY
                }
                R.id.show_this_week_asteroids_menu -> {
                    AsteroidFilters.WEEK
                }
                else -> {
                    AsteroidFilters.ALL
                }
            }
        )
        asteroid_recycler.scrollToPosition(0)
        return true
    }
}
