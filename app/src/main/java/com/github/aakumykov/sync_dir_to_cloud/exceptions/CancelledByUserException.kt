package com.github.aakumykov.sync_dir_to_cloud.exceptions

import kotlinx.coroutines.CancellationException

class CancelledByUserException : CancellationException()