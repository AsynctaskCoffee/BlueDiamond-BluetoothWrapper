package com.asynctaskcoffee.library

import android.annotation.SuppressLint
import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.asynctaskcoffee.library.databinding.DailogFragmentBinding

@SuppressLint("MissingPermission")
internal class BluetoothDialogFragment : DialogFragment(), AdapterView.OnItemClickListener {

    private var listener: DeviceSelectedListener? = null

    fun setOnDeviceSelectedListener(listener: DeviceSelectedListener) {
        this.listener = listener
    }


    interface DeviceSelectedListener {
        fun setOnDeviceSelectedListener(address: String)
    }

    private var list: ArrayList<BluetoothDevice>? = null

    companion object {
        val TAG = "BluetoothDialogFragment"

        @JvmStatic
        fun newInstance(list: ArrayList<BluetoothDevice>): BluetoothDialogFragment {
            val f = BluetoothDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList("list", list)
            f.arguments = args
            return f
        }
    }

    private var _binding: DailogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DailogFragmentBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        list = arguments?.getParcelableArrayList("list")
        val deviceList = ArrayList<String>()
        if ((list?.size ?: 0) > 0) {
            for (bt in list!!) {
                deviceList.add(bt.name + "\n" + bt.address.toString())
            }
        }
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                deviceList
            )
        binding.bluetoothListView.adapter = adapter
        binding.bluetoothListView.onItemClickListener = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val info = (p1 as TextView).text.toString()
        val address = info.substring(info.length - 17)
        listener?.setOnDeviceSelectedListener(address)
    }
}