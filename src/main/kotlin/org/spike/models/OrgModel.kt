package org.spike.models

data class OrgModel(
        val name : String,
        val uid : String,
        val children : List<OrgModel>,
        val parent : OrgModel?
)
