package edu.gvsu.cis.multi_timer

import edu.gvsu.cis.multi_timer.data.AppDAO
import edu.gvsu.cis.multi_timer.data.CloudSyncManager
import edu.gvsu.cis.multi_timer.viewModels.*
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun appModule(dao: AppDAO) = module {
    single<AppDAO> { dao }
    single { sessionManager() }
    single { CloudSyncManager(get()) }

    factory { HomeViewModel(get(), get())}
    factory { EditPlaysetsViewModel(get(), get()) }
    factory { EditPlayersViewModel(get(), get()) }
    factory { ActiveGameViewModel(get()) }
}

fun initKoin(dao: AppDAO) {
    try {
        startKoin {
            modules(appModule(dao))
        }
    } catch (_: Exception) { }
}

class sessionManager {
    var currentEditId: Int? = null
}