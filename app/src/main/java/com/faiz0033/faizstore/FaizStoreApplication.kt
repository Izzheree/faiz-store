package com.faiz0033.faizstore

import android.app.Application
import com.faiz0033.faizstore.di.AppContainer
import com.faiz0033.faizstore.di.DefaultAppContainer

class FaizStoreApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
