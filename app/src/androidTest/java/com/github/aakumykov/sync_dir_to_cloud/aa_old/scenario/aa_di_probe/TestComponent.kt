package com.github.aakumykov.sync_dir_to_cloud.aa_old.scenario.aa_di_probe

import dagger.Component

@Component(
    modules = [
        TestDaoModule::class
    ]
)
interface TestComponent {
    fun injectToQwertyScenario(qwertyScenario: QwertyScenario)
}

object TestAppComponentHolder {
    init {

    }
}