package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloud_auth")
class CloudAuth (
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "auth_token") val authToken: String
) {
    override fun toString(): String {
        return name
    }
}