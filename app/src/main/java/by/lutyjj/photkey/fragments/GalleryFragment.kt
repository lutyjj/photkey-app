package by.lutyjj.photkey.fragments

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.lutyjj.photkey.R
import by.lutyjj.photkey.databinding.GalleryOverviewBinding
import by.lutyjj.photkey.gallery.GalleryViewModel
import by.lutyjj.photkey.gallery.PhotoGridAdapter
import by.lutyjj.photkey.services.SyncService
import java.text.ParseException
import java.util.*


class GalleryFragment : Fragment() {
    private val viewModel: GalleryViewModel by viewModels()
    private val dateParser: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = GalleryOverviewBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.photosGrid.adapter = PhotoGridAdapter()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val swipeContainer = getView()?.findViewById<SwipeRefreshLayout>(R.id.swipe_container)
        swipeContainer?.setOnRefreshListener {
            viewModel.getPhotos()
            swipeContainer.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    try {
                        dateParser.parse(query)
                        viewModel.getByDate(query)
                    } catch (e: ParseException) {
                        viewModel.getByLocation(query)
                    }
                } else viewModel.getPhotos()
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sync -> {
                viewModel.getPhotos()
                activity?.startService(Intent(context, SyncService::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
