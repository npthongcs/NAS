package com.example.nsa

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.nsa.databinding.FragmentFilterBinding
import com.example.nsa.model.Filter
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FilterDialog : DialogFragment() {

    var beginDate: String? = null

    interface FilterDialogListener{
        fun finishFilterDialog(filter: Filter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {
        val etBeginDate = view.findViewById<EditText>(R.id.beginDate)
        val btnSave = view.findViewById<Button>(R.id.btnSaveFilter)
        val spinnerSort = view.findViewById<Spinner>(R.id.spinnerSort)
        val cbArts = view.findViewById<CheckBox>(R.id.cbArts)
        val cbFashion = view.findViewById<CheckBox>(R.id.cbFashion)
        val cbSports = view.findViewById<CheckBox>(R.id.cbSports)

        etBeginDate.setOnClickListener {
            Log.d("begin date","is clicked")
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DATE)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val datePickerDialog: DatePickerDialog? =
                context?.let { it1 ->
                    DatePickerDialog(it1, { _: DatePicker, i: Int, i1: Int, i2: Int ->
                        calendar.set(i,i1,i2)
                        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
                        beginDate = simpleDateFormat.format(calendar.time)
                        etBeginDate.setText(simpleDateFormat.format(calendar.time))
                    },year,month,day)
                }
            datePickerDialog?.show()
        }

        btnSave.setOnClickListener {
            val listener: FilterDialogListener = activity as FilterDialogListener
            val sort = spinnerSort.selectedItem.toString()
            val newsDesk: ArrayList<String> = ArrayList()
            if (cbArts.isChecked) newsDesk.add(cbArts.text.toString())
            if (cbFashion.isChecked) newsDesk.add(cbFashion.text.toString())
            if (cbSports.isChecked) newsDesk.add(cbSports.text.toString())
            val filter = Filter(beginDate,sort,newsDesk)
            listener.finishFilterDialog(filter)
            dismiss()
        }

    }

}