package org.spike.entities

import org.spike.uid.OrgUid
import org.neo4j.driver.types.Node

data class Org(
        val id: Long,
        val uid: OrgUid,
        val name: String,
        val childrenUids: List<OrgUid> = emptyList(),
        val parentUid: OrgUid? = null
) {
    companion object {
        fun from(node: Node): Org {
            return Org(node.id(), OrgUid(node.get("uid").toString()), node.get("name").asString())
        }
    }
}

