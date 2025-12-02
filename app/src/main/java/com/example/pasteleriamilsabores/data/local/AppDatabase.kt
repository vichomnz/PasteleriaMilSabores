package com.example.pasteleriamilsabores.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pasteleriamilsabores.model.Product

// Incrementamos la versión a 3 para aplicar cambios de esquema y limpiar errores de integridad
@Database(entities = [Product::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pasteleria_database"
                )
                .fallbackToDestructiveMigration() // Esto borra la BD antigua si cambia la versión
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
