package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository

interface CloudAuthChecker {
    suspend fun hasAuthWithName(name: String): Boolean
}