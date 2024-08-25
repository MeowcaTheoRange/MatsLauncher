package link.abtmtr.matkap

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class AppAdapter(private val context: Context, private val apps: List<AppInfo>) : BaseAdapter() {

    override fun getCount(): Int = apps.size

    override fun getItem(position: Int): Any = apps[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = convertView ?: inflater.inflate(R.layout.app, parent, false)

        val appIcon = view.findViewById<ImageView>(R.id.appImage)
        val appName = view.findViewById<TextView>(R.id.appName)

        val appInfo = getItem(position) as AppInfo
        appIcon.setImageDrawable(appInfo.icon)
        appName.text = appInfo.name

        // Set up click listener
        view.setOnClickListener {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
            } else {
                // Handle case where there is no launch intent
            }
        }

        return view
    }
}

data class AppInfo(val name: String, val icon: Drawable, val packageName: String)