package br.edu.ifsp.scl.sdm.listpad.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.sdm.listpad.R
import br.edu.ifsp.scl.sdm.listpad.model.Place
import com.squareup.picasso.Picasso

class PlaceAdapter (val placeList: ArrayList<Place>, val context: Context) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>(),
    Filterable {

    var listener: PlaceListener? = null
    var longListener: LongPlaceListener? = null
    var appContext : Context? = null

    var placeListFilterable = ArrayList<Place>()

    init {
        this.placeListFilterable = placeList
        this.appContext = context
    }

    fun setClickListener(listener: PlaceListener) {
        this.listener = listener
    }

    fun setLongClickListener(longListener: LongPlaceListener) {
        this.longListener = longListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_celula, parent, false);
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.namePlaceVH.text = placeListFilterable[position].name

        val db = DatabaseHelper(appContext!!)
        val country = db.selectCountry(placeListFilterable[position].idCountry!!)
        holder.nameCountryHV.text = country.name

        if(placeListFilterable[position].urlImage.isNotEmpty()) {
            val d = DownloadImageTask(holder.imvPlace)
            d.execute(placeListFilterable[position].urlImage)
        }

        if(placeListFilterable[position].visited!! > 0) {
            holder.imvVisited.setImageResource(R.mipmap.ic_bag_checked_round);
        }

    }

    override fun getItemCount(): Int {
        return placeListFilterable.size
    }

    inner class PlaceViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val namePlaceVH = view.findViewById<TextView>(R.id.tvNamePlace)
        val nameCountryHV = view.findViewById<TextView>(R.id.tvCountry)
        val imvPlace = view.findViewById<ImageView>(R.id.igvPlace)
        val imvVisited = view.findViewById<ImageView>(R.id.igvVisited)


        init {

            view.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }

            view.setOnLongClickListener {
                longListener?.onItemLongClick(adapterPosition)
                true
            }
        }
    }

    interface PlaceListener {
        fun onItemClick(pos: Int)
    }

    interface LongPlaceListener {
        fun onItemLongClick(pos: Int)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(criterio: CharSequence?): FilterResults {
                val charSearch = criterio.toString()
                if(charSearch.isEmpty()){
                    placeListFilterable = placeList
                }
                else {
                    var resultList = ArrayList<Place>()
                    for(place in placeList) {
                        if(place.name.lowercase().contains(charSearch.lowercase())) {
                            resultList.add(place)
                        }
                    }
                    placeListFilterable = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = placeListFilterable
                filterResults.count = placeListFilterable.size
                return filterResults
            }

            override fun publishResults(criterio: CharSequence?, filtersResult: FilterResults?) {
                placeListFilterable = filtersResult?.values as ArrayList<Place>
                notifyDataSetChanged()
            }
        }
    }
}