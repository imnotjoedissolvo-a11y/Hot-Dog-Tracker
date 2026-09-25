package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.HotDogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HotDogWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val sharedPrefs = context.getSharedPreferences("hotdog_prefs", Context.MODE_PRIVATE)
        val count = sharedPrefs.getInt("today_count", 0)

        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, count)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_INCREMENT) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val repository = HotDogRepository(db.hotDogDao(), context)
                    repository.increment()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        const val ACTION_INCREMENT = "com.example.hotdogcounter.ACTION_INCREMENT"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            count: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.hotdog_widget)

            // Update text
            views.setTextViewText(R.id.widget_count_text, count.toString())
            val labelText = if (count == 1) "hot dog eaten" else "hot dogs eaten"
            views.setTextViewText(R.id.widget_label, labelText)

            // Pending intent to open main activity
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openAppPendingIntent)

            // Pending intent for +1 button
            val incrementIntent = Intent(context, HotDogWidgetProvider::class.java).apply {
                action = ACTION_INCREMENT
            }
            val incrementPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                incrementIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_plus, incrementPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context, count: Int) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, HotDogWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName) ?: return

            for (appWidgetId in appWidgetIds) {
                updateWidget(context, appWidgetManager, appWidgetId, count)
            }
        }
    }
}
