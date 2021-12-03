package br.edu.ifsp.scl.sdm.listpad.model

import java.io.Serializable

class Place (
    var id: Int? = null,
    var idCountry: Int? = null,
    var name: String = "",
    var description: String = "",
    var urlImage: String = "",
    var visited: Int? = null, //0 Not Visited , 1 Visited
) : Serializable