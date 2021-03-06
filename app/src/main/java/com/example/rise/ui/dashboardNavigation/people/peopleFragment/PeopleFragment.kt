package com.example.rise.ui.dashboardNavigation.people.peopleFragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rise.R
import com.example.rise.baseclasses.BaseFragment
import com.example.rise.helpers.AppConstants
import com.example.rise.item.PersonItem
import com.example.rise.ui.dashboardNavigation.people.chatActivity.ChatActivity
import com.example.rise.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*
import org.koin.android.ext.android.get

class PeopleFragment : BaseFragment<PeopleViewModel>() {

    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var peopleSection: Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        userListenerRegistration = FirestoreUtil.addUsersListener(this.requireActivity(), this::updateRecyclerView)
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {

        fun init() {
            recycler_view_people.apply {
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if (shouldInitRecyclerView)
            init()
        else updateItems()
    }

    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is PersonItem) {
            val intent = Intent(this.context, ChatActivity::class.java)
            intent.putExtra(AppConstants.USER_NAME,item.person.name)
            intent.putExtra(AppConstants.USER_ID,item.userId)

            startActivity(
                intent
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun createViewModel() {
        viewModel = get()
    }
}