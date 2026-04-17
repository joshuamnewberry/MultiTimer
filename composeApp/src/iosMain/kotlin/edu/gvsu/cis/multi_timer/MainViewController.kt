package edu.gvsu.cis.multi_timer

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import edu.gvsu.cis.multi_timer.data.App
import edu.gvsu.cis.multi_timer.data.AppDB
import edu.gvsu.cis.multi_timer.data.getDatabaseInstance
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    CompositionLocalProvider() {
        val db: AppDB = getDatabaseInstance(getDatabaseBuilder())
        App(db.getDao())
    }
}