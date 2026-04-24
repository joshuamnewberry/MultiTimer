package edu.gvsu.cis.multi_timer

import androidx.compose.ui.window.ComposeUIViewController
import edu.gvsu.cis.multi_timer.data.AppDB
import edu.gvsu.cis.multi_timer.data.getDatabaseInstance
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    val db: AppDB = getDatabaseInstance(getDatabaseBuilder())
    val dao = db.getDao()

    initKoin(dao)

    App()
}