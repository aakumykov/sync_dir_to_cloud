package com.github.aakumykov.sync_dir_to_cloud.bb_new.fs_path

object FilePathSamples {
    private const val BASE_PATH="/base/path"

    const val EMPTY_PATH=""
    const val WHITE_SPACE_PATH=" "
    const val TAB_PATH="    "
    const val TABS_AND_SPACES_PATH="           "
    const val ROOT_PATH="/"

    const val PATH_WITH_SPACES="/path/to my/file with complex name.txt"

    const val SIMPLE_FILE_NAME="qwerty"
    const val COMPLEX_FILE_NAME="My File 1.txt"

    const val RELATIVE_DEEP_PATH="relative/path/to/file"
    const val RELATIVE_PATH_WITH_MULTI_SLASHES="relative//path////to/file"
    const val RELATIVE_DEEP_PATH_TAIL_SLASH="relative/path/to/file/"
    const val RELATIVE_PATH_WITH_MULTI_SLASHES_AND_TAIL_SLASH="relative//path////to/file/"

    const val DEEP_PATH_TO_FILE="$BASE_PATH/path/to/file"
    const val DEEP_PATH_TO_DIR_WITHOUT_SLASH="$BASE_PATH/path/to/dir"
    const val DEEP_PATH_TO_DIR_WITH_SLASH="$BASE_PATH/path/to/dir/"

    const val MULTI_SLASH_DEEP_PATH_TO_FILE="$BASE_PATH/path//to///file"
    const val MULTI_SLASH_DEEP_PATH_TO_DIR_WITH_TAIL_SLASH="$BASE_PATH//path/to//dir///"
    const val MULTI_SLASH_DEEP_PATH_TO_DIR_WITHOUT_TAIL_SLASH="$BASE_PATH//path/to//dir"

    const val PATH_TO_FILE_OUTSIDE_BASE_PATH="/path/to/other/file"
    const val PATH_TO_DIR_OUTSIDE_BASE_PATH_WITHOUT_SLASH="/path/to/other/dir"
    const val PATH_TO_DIR_OUTSIDE_BASE_PATH_WITH_SLASH="/path/to/other/dir/"
}