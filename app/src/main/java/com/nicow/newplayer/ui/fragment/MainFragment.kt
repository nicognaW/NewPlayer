package com.nicow.newplayer.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicow.newplayer.R
import com.nicow.newplayer.logic.Repository
import com.nicow.newplayer.ui.activity.MainActivity
import com.nicow.newplayer.ui.adapter.MusicListAdapter
import com.nicow.newplayer.ui.viewmodel.TopListViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private lateinit var adapter: MusicListAdapter
    private lateinit var currentListItem: MenuItem
    private lateinit var loadingLayout: FrameLayout
    private var tickTockListItem: MenuItem? = null

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: TopListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        currentListItem = menu.findItem(R.id.menu_item_hostsong)
        tickTockListItem = menu.findItem(R.id.menu_item_ticktock)
        tickTockListItem?.isVisible = viewModel.currentApi.value != Repository.API.MUSICAPI
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        fun changeApi(item: MenuItem): Boolean {
            when (viewModel.currentApi.value) {
                Repository.API.MUSICAPI -> {
                    item.title = (resources.getString(R.string.switch_2_music_api))
                    viewModel.currentApi.value = Repository.API.NATIVEWEB
                }
                Repository.API.NATIVEWEB -> {
                    item.title = (resources.getString(R.string.switch_2_spider_api))
                    viewModel.currentApi.value = Repository.API.MUSICAPI
                }
            }
            return false
        }

        fun changeList(item: MenuItem, LIST: Repository.TOPLIST): Boolean {
            loadingLayout.visibility = View.VISIBLE

            currentListItem.title = Regex(".*榜").find(currentListItem.title.toString())!!.value
            item.title = Regex(".*榜").find(item.title.toString())!!.value + "\uD83E\uDDFE"
            currentListItem = item
            if (viewModel.currentListIdLiveData.value != LIST) {
                viewModel.topListLiveData.value?.clear()
                adapter.notifyDataSetChanged()
                viewModel.switchTopList(LIST)
                return true
            } else {
                return false
            }
        }

        when (item.itemId) {
            R.id.menu_item_switch_API -> {
                return changeApi(item)
            }
            R.id.menu_item_hostsong -> {
                return changeList(item, Repository.TOPLIST.HOTSONG)
            }
            R.id.menu_item_newsong -> {
                return changeList(item, Repository.TOPLIST.NEWSONG)
            }
            R.id.menu_item_ticktock -> {
                return changeList(item, Repository.TOPLIST.TICKTOCK)
            }
            R.id.menu_item_ACG -> {
                return changeList(item, Repository.TOPLIST.ACG)
            }
            R.id.menu_item_billboard -> {
                return changeList(item, Repository.TOPLIST.BILLBOARD)
            }
            R.id.menu_item_beatport -> {
                return changeList(item, Repository.TOPLIST.BEATPORT)
            }
            else -> return false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadingLayout = load as FrameLayout

        viewModel = ViewModelProviders.of(this).get(TopListViewModel::class.java)

        val layoutManager = LinearLayoutManager(this.context)
        musicListView.layoutManager = layoutManager

        val musicListLiveData = viewModel.topListLiveData
        adapter = MusicListAdapter(musicListLiveData.value!!)
        musicListView.adapter = adapter

        viewModel.run {
            currentListIdLiveData.observe(viewLifecycleOwner, Observer {
                (context as MainActivity).supportActionBar?.title = "${it}榜"
            })

            topListLiveData.observe(viewLifecycleOwner, Observer {
                adapter.notifyDataSetChanged()
            })

            currentApi.observe(viewLifecycleOwner, Observer {
                tickTockListItem?.isVisible = it != Repository.API.MUSICAPI
            })

            topListMusicLiveData.observe(viewLifecycleOwner, Observer { it ->
                loadingLayout.visibility = View.GONE
                if (it.list == viewModel.currentListIdLiveData.value) {
                    this.topListLiveData.value!!.add(it)
                    val index = (this.topListLiveData.value?.size)?.minus(1)
                    index?.let { index1 ->
                        adapter.notifyItemInserted(index1)
                        if (viewModel.currentApi.value == Repository.API.NATIVEWEB) {
                            viewModel.getArtistByUrl(it.url) { it1 ->
                                if (index1 + 1 <= this.topListLiveData.value!!.size) {
                                    this.topListLiveData.value!![index1].artist = it1.toString()
                                    adapter.notifyItemChanged(index1)
                                }
                            }
                        } else {
                            return@Observer
                        }
                    }
                }
            })
        }

    }
}

