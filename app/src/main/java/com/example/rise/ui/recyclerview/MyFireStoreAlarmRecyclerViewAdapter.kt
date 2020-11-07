package com.example.rise.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rise.models.Alarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.recycler_alarm_item.view.*
import timber.log.Timber


open class MyFireStoreAlarmRecyclerViewAdapter(
    mQuery: Query,
    context: Context

) : FirestoreAdapterBase<MyFireStoreAlarmRecyclerViewAdapter.ViewHolder>(mQuery, context) {

    lateinit var alarm: Alarm
    open var otherUsrId: String? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position)/*, mListener*/)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.example.rise.R.layout.recycler_alarm_item, parent, false)
        itemCount
        return ViewHolder(view)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        fun bind(
            snapshot: DocumentSnapshot
        ) {
            alarm = snapshot.toObject(Alarm::class.java)!!

            if (alarm.timeInSeconds.rem(60) >= 10) {
                mView.time_remaining.text =
                    alarm.timeInSeconds.div(60).toString() + " : " + alarm.timeInSeconds.rem(60).toString()
                mView.display_name.text = alarm.userName
            } else {
                mView.time_remaining.text = alarm.timeInSeconds.div(60).toString() + " : " + " 0" + alarm.timeInSeconds.rem(60).toString()
                mView.display_name.text = alarm.userName
            }

            mView.deleteButton.setOnClickListener { v ->
                //if otherUsrID!-= null it means we're accessing dashboard of other user, otherwise we're at our own dashboard

                if (otherUsrId != null) {
                    FirebaseFirestore.getInstance().collection("/users")
                        .document("$otherUsrId").collection("/alarms").document(snapshot.id)
                        .delete()
                        .addOnSuccessListener { Timber.d("DocumentSnapshot successfully deleted!") }
                        .addOnFailureListener { e -> Timber.w("Error deleting document") }
                } else {
                    FirebaseFirestore.getInstance()
                        .collection("/users")
                        .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .collection("/alarms")
                        .document(snapshot.id)
                        .delete()
                        .addOnSuccessListener { Timber.d("DocumentSnapshot successfully deleted!") }
                        .addOnFailureListener { e -> Timber.w("Error deleting document") }
                }
            }
        }
    }
}