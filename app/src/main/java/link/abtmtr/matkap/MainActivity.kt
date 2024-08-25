package link.abtmtr.matkap

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var textClock: TextView
    private lateinit var textDate: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val updateDTRunnable = object : Runnable {
        override fun run() {
            updateClock()
            updateDate()
            handler.postDelayed(this, 500) // Update every second
        }
    }

    private val launcherPackageName: String by lazy { packageName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val linearLayout: LinearLayout = findViewById(R.id.parent)

        ViewCompat.setOnApplyWindowInsetsListener(linearLayout) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        val gridView: GridView = findViewById(R.id.apps_container)

        val apps = getInstalledApps()
        val adapter = AppAdapter(this, apps)
        gridView.adapter = adapter

        textClock = findViewById(R.id.time)
        textDate = findViewById(R.id.date)
        handler.post(updateDTRunnable)
    }

    private fun getInstalledApps(): List<AppInfo> {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = pm.queryIntentActivities(intent, 0)
        val apps = mutableListOf<AppInfo>()

        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName

            // Skip the launcher app itself
            if (packageName == launcherPackageName) continue

            val appName = resolveInfo.loadLabel(pm).toString()
            val appIcon = resolveInfo.loadIcon(pm)
            apps.add(AppInfo(appName, appIcon, packageName))
        }

        // Sort apps alphabetically by name
        apps.sortBy { it.name }


        return apps
    }

    private fun updateClock() {
        val dateFormat = SimpleDateFormat("hh:mm aaa", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        textClock.text = currentTime
    }

    private fun updateDate() {
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        textDate.text = currentTime
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateDTRunnable)
    }
}