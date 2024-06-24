package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target

import javax.inject.Inject

class DatabaseToStorageWriter @Inject constructor(

) {
    /* I. Синхронизировать каталоги
            1. Удалить удалённое (зависит от стратегии)
            2. Копировать изменившееся (стратегия)
            3. Восстановить пропавшее (стратегия)
       II. Синхронизировать файлы*/
}