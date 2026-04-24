package edu.gvsu.cis.multi_timer

import edu.gvsu.cis.multi_timer.data.AppDAO
import edu.gvsu.cis.multi_timer.viewModels.*
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun appModule(dao: AppDAO) = module {
    single<AppDAO> { dao }
    single { PlaysetSessionManager() }

    factory { HomeViewModel(get()) }
    factory { SettingsViewModel(get()) }
    factory { EditPlaysetsViewModel(get(), get()) }
    factory { ActiveGameViewModel(get()) }
}

fun initKoin(dao: AppDAO) {
    try {
        startKoin {
            modules(appModule(dao))
        }
    } catch (e: Exception) { }
}

class PlaysetSessionManager {
    var currentEditId: Int? = null
}