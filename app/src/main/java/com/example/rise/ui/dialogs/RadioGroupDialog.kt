package com.example.rise.ui.dialogs

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import com.example.rise.models.RadioItem
import com.example.rise.R
import com.example.rise.extensions.onGlobalLayout
import com.example.rise.extensions.setupDialogStuff
import kotlinx.android.synthetic.main.dialog_radio_group.view.*

class RadioGroupDialog(val activity: AppCompatActivity, val items: ArrayList<RadioItem>, val checkedItemId: Int = -1, val titleId: Int = 0,
                       showOKButton: Boolean = false, val cancelCallback: (() -> Unit)? = null, val callback: (newValue: Any) -> Unit) {
    private val dialog: AlertDialog
    private var wasInit = false
    private var selectedItemId = -1

    init {
        val view = activity.layoutInflater.inflate(R.layout.dialog_radio_group, null)
        view.dialog_radio_group.apply {
            for (i in 0 until items.size) {
                val radioButton = (activity.layoutInflater.inflate(R.layout.radio_button, null) as RadioButton).apply {
                    text = items[i].title
                    isChecked = items[i].id == checkedItemId
                    id = i
                    setOnClickListener { itemSelected(i) }
                }

                if (items[i].id == checkedItemId) {
                    selectedItemId = i
                }

                addView(radioButton, RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }
        }

        val builder = AlertDialog.Builder(activity)
            .setOnCancelListener { cancelCallback?.invoke() }

        if (selectedItemId != -1 && showOKButton) {
            builder.setPositiveButton(R.string.ok) { dialog, which -> itemSelected(selectedItemId) }
        }

        dialog = builder.create().apply {
            activity.setupDialogStuff(view, this, titleId)
        }

        if (selectedItemId != -1) {
            view.dialog_radio_holder.apply {
                onGlobalLayout {
                    scrollY = view.dialog_radio_group.findViewById<View>(selectedItemId).bottom - height
                }
            }
        }

        wasInit = true
    }

    private fun itemSelected(checkedId: Int) {
        if (wasInit) {
            callback(items[checkedId].value)
            dialog.dismiss()
        }
    }
}
