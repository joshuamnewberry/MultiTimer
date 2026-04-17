package edu.gvsu.cis.multi_timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val db: AppDB = getDatabaseInstance(getDatabaseBuilder(applicationContext))
            App(db.getDao())
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppAndroidPreview() {
    val fakeDao = FakeAppDAO()
    App(fakeDao)
}