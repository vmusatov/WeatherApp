package com.example.weatherapp.ui.locations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemManageLocationsBinding
import com.example.weatherapp.model.LocationDto
import com.example.weatherapp.model.TempUnit
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class ManageLocationListAdapter(
    private val addLocationListener: AddLocationListener
) : RecyclerView.Adapter<ManageLocationListAdapter.ViewHolder>(), View.OnClickListener {

    private var data: MutableList<LocationDto> = mutableListOf()
    private var dataInfo: MutableSet<LocationInfo> = mutableSetOf()
    var tempUnit: TempUnit = TempUnit.DEFAULT

    fun updateLocationsInfo(info: Collection<LocationInfo>) {
        dataInfo.addAll(info)
        notifyDataSetChanged()
    }

    fun update(data: List<LocationDto>) {
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemManageLocationsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val item = data[position]
            val res = holder.itemView.resources

            root.tag = item

            locationName.text = item.name
            locationDesc.text = "${item.region}, ${item.country}"

            val itemInfo = dataInfo.firstOrNull { it.locationName == item.name }
            if (itemInfo != null) {
                if (tempUnit == TempUnit.C) {
                    locationTemp.text = res.getString(R.string.degree, itemInfo.tempC.roundToInt())
                } else {
                    locationTemp.text = res.getString(R.string.degree, itemInfo.tempF.roundToInt())
                }

                Picasso.get()
                    .load(itemInfo.conditionIconUrl)
                    .into(locationCondition)
            }

            root.setOnClickListener(this@ManageLocationListAdapter)
            root.setOnLongClickListener {


                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(val binding: ItemManageLocationsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onClick(v: View) {
        val location = v.tag as LocationDto
        addLocationListener.invoke(location)
    }
}
