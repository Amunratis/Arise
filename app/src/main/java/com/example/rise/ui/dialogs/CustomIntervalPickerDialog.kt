package com.example.rise.ui.dialogs

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.rise.R
import com.example.rise.extensions.beVisibleIf
import com.example.rise.extensions.hideKeyboard
import com.example.rise.helpers.DAY_SECONDS
import com.example.rise.helpers.HOUR_SECONDS
import com.example.rise.helpers.MINUTE_SECONDS
import com.example.rise.extensions.*

import kotlinx.android.synthetic.main.dialog_custom_interval_picker.view.*


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CustomIntervalPickerDialog(val activity: AppCompatActivity, val selectedSeconds: Int = 0, val showSeconds: Boolean = false, val callback: (minutes: Int) -> Unit) {
    var dialog: AlertDialog
    var view = (activity.layoutInflater.inflate(R.layout.dialog_custom_interval_picker, null) as ViewGroup)

    init {
        view.apply {
            dialog_radio_seconds.beVisibleIf(showSeconds)
            when {
                selectedSeconds == 0 -> dialog_radio_view.check(R.id.dialog_radio_minutes)
                selectedSeconds % DAY_SECONDS == 0 -> {
                    dialog_radio_view.check(R.id.dialog_radio_days)
                    dialog_custom_interval_value.setText((selectedSeconds / DAY_SECONDS).toString())
                }
                selectedSeconds % HOUR_SECONDS == 0 -> {
                    dialog_radio_view.check(R.id.dialog_radio_hours)
                    dialog_custom_interval_value.setText((selectedSeconds / HOUR_SECONDS).toString())
                }
                selectedSeconds % MINUTE_SECONDS == 0 -> {
                    dialog_radio_view.check(R.id.dialog_radio_minutes)
                    dialog_custom_interval_value.setText((selectedSeconds / MINUTE_SECONDS).toString())
                }
                else -> {
                    dialog_radio_view.check(R.id.dialog_radio_seconds)
                    dialog_custom_interval_value.setText(selectedSeconds.toString())
                }
            }
        }

        dialog = AlertDialog.Builder(activity)
            .setPositiveButton(R.string.ok) { dialogInterface, i -> confirmReminder() }
            .setNegativeButton(R.string.cancel, null)
            .create().apply {
                activity.setupDialogStuff(view, this) {
                    showKeyboard(view.dialog_custom_interval_value)
                }
            }
    }

    fun AlertDialog.showKeyboard(editText: EditText) {
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        editText.apply {
            requestFocus()
            onGlobalLayout {
                setSelection(text.toString().length)
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun confirmReminder() {
        //TODO teporary fix here
        val value = view.dialog_custom_interval_value.transitionName
        val multiplier = getMultiplier(view.dialog_radio_view.checkedRadioButtonId)
        val minutes = Integer.valueOf(if (value.isEmpty()) "0" else value)
        callback(minutes * multiplier)
        activity.hideKeyboard()
        dialog.dismiss()
    }

    private fun getMultiplier(id: Int) = when (id) {
        R.id.dialog_radio_days -> DAY_SECONDS
        R.id.dialog_radio_hours -> HOUR_SECONDS
        R.id.dialog_radio_minutes -> MINUTE_SECONDS
        else -> 1
    }
}