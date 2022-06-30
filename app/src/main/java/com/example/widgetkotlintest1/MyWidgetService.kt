package com.example.widgetkotlintest1

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.testwidget1.model.Stock
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MyWidgetService : RemoteViewsService() {
    private val API_URL = "http://10.0.2.2:5000/stocks"
    private var arrayList: ArrayList<Stock> = ArrayList<Stock>()
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return MyWidgetRemoteViewsFactory(applicationContext)
    }
    internal inner class MyWidgetRemoteViewsFactory(private val context: Context) :
        RemoteViewsFactory {
        private var stockList: ArrayList<Stock>
        override fun onCreate() {
            stockList = arrayList
        }

        override fun onDataSetChanged() {
            GetStockPriceFromAPI().execute(API_URL)
//            returned arrayList should look like this
//            [
//                {
//                    "BIDPRICE":"37.89",
//                    "CHANGE":"-0.14",
//                    "ISUP":"-1",
//                    "PERCENT_CHANGE":"-0.14",
//                    "SYMBOL":"YHOO",
//                    "_ID":"1"
//                },
//                {
//                    "BIDPRICE":"104.14",
//                    "CHANGE":"1.39",
//                    "ISUP":"1",
//                    "PERCENT_CHANGE":"1.39",
//                    "SYMBOL":"AAPL",
//                    "_ID":"2"
//                }
//            ]
            stockList = arrayList
        }

        override fun onDestroy() {
            if (stockList != null) {
                stockList.clear()
            }
        }

        override fun getCount(): Int {
            return if (stockList != null) {
                stockList.size
            } else 0
        }

        override fun getViewAt(position: Int): RemoteViews {
            val remoteView = RemoteViews(context.packageName, R.layout.widget_list_item)

            if (stockList.size >= 1 ) {
                val selectedStock = stockList[position]
                remoteView.setTextViewText(R.id.stockSymbol, selectedStock.SYMBOL)
                Log.d("getSYMBOL", selectedStock.SYMBOL)
                remoteView.setTextViewText(R.id.stockPrice, selectedStock.BIDPRICE)
                Log.d("getBIDPRICE", selectedStock.BIDPRICE)
                if (selectedStock.ISUP.contains("-")) {
                    remoteView.setImageViewResource(R.id.stockUpDownImg, R.drawable.stockdown)
                } else {
                    remoteView.setImageViewResource(R.id.stockUpDownImg, R.drawable.stockup)
                }

                // this is to add item click listener
                val extras = Bundle()
                extras.putInt(MyWidget.SELECTED_STOCK_POSITION, position)
                extras.putString(MyWidget.SELECTED_STOCK,selectedStock.SYMBOL)
                val fillInIntent = Intent()
                fillInIntent.putExtras(extras)
                remoteView.setOnClickFillInIntent(R.id.widgetListItem,fillInIntent)
            }
            return remoteView
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return if (stockList != null) {
                stockList.size
            } else 0
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        init {
            stockList = arrayList
        }
        private inner class GetStockPriceFromAPI : AsyncTask<String?, String?, String?>() {
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg p0: String?): String? {
                var connection: HttpURLConnection? = null
                var reader: BufferedReader? = null
                try {
                    val url = URL(p0[0])
                    connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    val stream = connection!!.inputStream
                    reader = BufferedReader(InputStreamReader(stream))
                    val buffer = StringBuffer()
//                    var line = ""
                    reader.forEachLine {
                        buffer.append(
                            "$it".trimIndent()
                        )
                        Log.d("Response: ", "> $it") // check Logcat Debug
                    }
                    val jsonArray = JSONArray(buffer.toString())
                    if (jsonArray != null) {
                        val gson = Gson()
                        val type = object : TypeToken<List<Stock?>?>() {}.type
                        val stockList = gson.fromJson<List<Stock>>(buffer.toString(), type)
                        arrayList.clear()
                        for (stock in stockList) {
                            arrayList.add(stock)
                        }
                    }
                    Log.d("arrayList", arrayList.toString())
                    return "yay"
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection?.disconnect()
                    try {
                        reader?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
            }
        }
    }
}