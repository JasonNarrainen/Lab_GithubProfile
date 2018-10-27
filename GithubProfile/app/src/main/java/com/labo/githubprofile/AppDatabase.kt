package com.labo.githubprofile

import android.app.Application
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [User::class], version = 1)
abstract class AppDatabase: RoomDatabase(){

    abstract fun userDao(): UserDao

    companion object {
        var appDatabaseInstance: AppDatabase? = null

        fun getDbInstance(mainActivity: MainActivity): AppDatabase{

            if (appDatabaseInstance == null){

                synchronized(AppDatabase::class){

                    appDatabaseInstance = Room.databaseBuilder(mainActivity.applicationContext, AppDatabase::class.java,
                        "app.db").build()
                }
            }

            return appDatabaseInstance!!
        }
    }
}