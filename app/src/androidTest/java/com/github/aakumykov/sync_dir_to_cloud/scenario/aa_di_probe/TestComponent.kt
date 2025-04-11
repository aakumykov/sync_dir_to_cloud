package com.github.aakumykov.sync_dir_to_cloud.scenario.aa_di_probe

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