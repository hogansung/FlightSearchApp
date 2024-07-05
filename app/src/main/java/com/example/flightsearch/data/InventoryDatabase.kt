package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun airportDao(): AirportDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null
        private var dbName: String = "flight_search_database"

        fun getDatabase(context: Context): InventoryDatabase {
            // If the Instance is not null, return it; otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                val dbFile = context.getDatabasePath(dbName)

                // If the dbFile does not exist, copy it from `assets` folder.
                if (!dbFile.exists()) {
                    Room.databaseBuilder(
                        context,
                        InventoryDatabase::class.java,
                        dbName
                    )
                        .createFromAsset("database/flight_search.db")
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { Instance = it }
                } else {
                    Room.databaseBuilder(
                        context.applicationContext,
                        InventoryDatabase::class.java,
                        dbName
                    )
                        .build()
                        .also { Instance = it }
                }
            }
        }
    }
}
