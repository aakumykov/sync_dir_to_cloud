package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class SyncObjectCopierWithRegistrator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val syncObjectRegistratorAssistedFactory: SyncObjectRegistratorAssistedFactory,
//    private val syncObjectStateChanger: SyncObjectStateChanger,
) {

/*
Копирование:
1) копировать файл / создать папку в хранилище;
2) пометить оба объекта как успешно прошедшие обработку.
3) добавить в БД запись о SyncObject в хранилище;

Удаление:
1) удалить;
2) пометить как удалённый в БД в соответствующем хранилище.

Бекап:
1) забекапить
 */
}