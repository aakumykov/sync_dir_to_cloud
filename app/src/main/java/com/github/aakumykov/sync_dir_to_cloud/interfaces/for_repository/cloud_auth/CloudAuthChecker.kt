package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth

interface CloudAuthChecker {
    suspend fun hasAuthWithName(name: String): Boolean
}