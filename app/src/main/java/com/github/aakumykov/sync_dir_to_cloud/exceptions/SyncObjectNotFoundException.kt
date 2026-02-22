package com.github.aakumykov.sync_dir_to_cloud.exceptions

class SyncObjectNotFoundException(objectId: String) : Exception("Object id='$objectId")