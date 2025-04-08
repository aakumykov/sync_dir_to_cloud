package com.github.aakumykov.sync_dir_to_cloud.scenario.checks.database

import android.provider.ContactsContract
import com.github.aakumykov.sync_dir_to_cloud.common.dao_set.DaoSet
import com.github.aakumykov.sync_dir_to_cloud.scenario.common.DbScenario
import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert

class NoChangesInSyncObjectsDB(
    daoSet: DaoSet,
    private val codeToRun: () -> Unit,
) : DbScenario(daoSet) {

    override val steps: TestContext<Unit>.() -> Unit
        get() = {
            val objectList1 = syncObjectDAO.testListAllObjects()
            runTest { codeToRun.invoke() }
            val objectList2 = syncObjectDAO.testListAllObjects()
            Assert.assertEquals(objectList1.size, objectList2.size)
        }
}
