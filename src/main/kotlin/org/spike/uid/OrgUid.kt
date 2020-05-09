package org.spike.uid

import java.util.*

class OrgUid {
    companion object {
        private const val PREFIX = 'O'
    }

    constructor(uid : String) {
        uuid = UUID.fromString(uid.substring(1, uid.length-1))
    }

    constructor() {
        uuid = UUID.randomUUID()
    }

    private val uuid : UUID

    override fun toString(): String {
        return "$PREFIX$uuid"
    }
}