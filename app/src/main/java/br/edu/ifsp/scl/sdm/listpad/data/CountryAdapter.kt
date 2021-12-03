package br.edu.ifsp.scl.sdm.listpad.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.sdm.listpad.R
import br.edu.ifsp.scl.sdm.listpad.model.Continent
import br.edu.ifsp.scl.sdm.listpad.model.Country

class CountryAdapter(val countryList: ArrayList<Country>) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>(), Filterable {

    var listener: CountryListener? = null
    var longListener: LongCountryListener? = null

    var countryListFilterable = ArrayList<Country>()

    init {
        this.countryListFilterable = countryList
    }

    fun setClickListener(listener: CountryListener) {
        this.listener = listener
    }

    fun setLongClickListener(longListener: LongCountryListener) {
        this.longListener = longListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_celula, parent, false);
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.nameVH.text = countryListFilterable[position].name
        holder.abbHV.text = countryListFilterable[position].abbreviation

        val continentName = when(countryListFilterable[position].idContinent) {
            Continent.WORLD.ordinal -> Continent.WORLD.countryName
            Continent.AFRICA.ordinal -> Continent.AFRICA.countryName
            Continent.AMERICAS.ordinal -> Continent.AMERICAS.countryName
            Continent.ASIA.ordinal -> Continent.ASIA.countryName
            Continent.EUROPE.ordinal -> Continent.EUROPE.countryName
            Continent.OCEANIA.ordinal -> Continent.OCEANIA.countryName
            else -> "Invalid Continent"
        }

        holder.continentHV.text = continentName

        if(countryListFilterable[position].urgent!! > 0) {
            holder.urgentHV.setImageResource(R.mipmap.ic_urgent);
        }

    }

    override fun getItemCount(): Int {
        return countryListFilterable.size
    }

    inner class CountryViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val nameVH = view.findViewById<TextView>(R.id.tvName)
        val abbHV = view.findViewById<TextView>(R.id.tvAbbreviation)
        val continentHV = view.findViewById<TextView>(R.id.tvContinent)
        val urgentHV = view.findViewById<ImageView>(R.id.igvUrgent)

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

    interface CountryListener {
        fun onItemClick(pos: Int)
    }

    interface LongCountryListener {
        fun onItemLongClick(pos: Int)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(criterio: CharSequence?): FilterResults {
                val charSearch = criterio.toString()
                if(charSearch.isEmpty()){
                    countryListFilterable = countryList
                }
                else {
                    var resultList = ArrayList<Country>()
                    for(country in countryList) {
                        if(country.name.lowercase().contains(charSearch.lowercase())) {
                            resultList.add(country)
                        }
                    }
                    countryListFilterable = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryListFilterable
                filterResults.count = countryListFilterable.size
                return filterResults
            }

            override fun publishResults(criterio: CharSequence?, filtersResult: FilterResults?) {
                countryListFilterable = filtersResult?.values as ArrayList<Country>
                notifyDataSetChanged()
            }
        }
    }
}