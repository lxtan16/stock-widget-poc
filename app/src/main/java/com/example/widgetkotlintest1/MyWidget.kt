package com.example.widgetkotlintest1

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ListView
import android.widget.RemoteViews
import android.widget.Toast

/**
 * Implementation of App Widget functionality.
 */
class MyWidget : AppWidgetProvider() {
    private val ACTION_WIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"
    private val ACTION_ITEM_CLICK = "com.example.widgetkotlintest1.ACTION_ITEM_CLICK"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_WIDGET_UPDATE -> {
                Toast.makeText(context, "ACTION_WIDGET_UPDATE", Toast.LENGTH_LONG).show()
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds =
                    appWidgetManager.getAppWidgetIds(ComponentName(context, MyWidget::class.java))
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView)
            }
            ACTION_ITEM_CLICK -> {
                val position:Int = intent.getIntExtra(SELECTED_STOCK_POSITION,0)
                val selectedStockSymbol:String = intent.getStringExtra(SELECTED_STOCK).toString()

                Log.d("Selected stock", selectedStockSymbol)
                Log.d("Selected stock position", position.toString())
                Toast.makeText(context, "On click $selectedStockSymbol", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val intent = Intent(context, MyWidgetService::class.java)
        val views = RemoteViews(context.packageName, R.layout.my_widget)
        views.setRemoteAdapter(R.id.widgetListView, intent)
        views.setEmptyView(R.id.widgetListView, R.id.widgetEmptyViewText)

        val refreshIntent = Intent(context, MyWidget::class.java)
        refreshIntent.action = ACTION_WIDGET_UPDATE
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, 0)
        views.setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)

        val itemClickIntent = Intent(context,MyWidget::class.java)
        itemClickIntent.action = ACTION_ITEM_CLICK
        val itemClickPendingIntent = PendingIntent.getBroadcast(context,0,itemClickIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        views.setPendingIntentTemplate(R.id.widgetListView,itemClickPendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object{
        const val SELECTED_STOCK = "com.example.widgetkotlintest1.SELECTED_STOCK"
        const val SELECTED_STOCK_POSITION = "com.example.widgetkotlintest1.SELECTED_STOCK_POSITION"
    }
}

