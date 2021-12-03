package br.edu.ifsp.scl.sdm.listpad.model

import java.io.Serializable

class Country(
    var id: Int? = null,
    var idContinent: Int? = null,
    var name: String = "",
    var abbreviation: String = "",
    var urlImage: String = "",
    var urgent: Int? = null, //0 Not Urgent , 1 Urgent
) : Serializable
