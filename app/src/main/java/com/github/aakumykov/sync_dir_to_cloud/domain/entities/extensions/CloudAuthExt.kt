package com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.google.gson.Gson

fun CloudAuth.toJSON(gson: Gson): String  =  gson.toJson(this, CloudAuth::class.java)

