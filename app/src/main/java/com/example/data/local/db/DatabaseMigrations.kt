package com.example.data.local.db

import androidx.room.migration.Migration

/**
 * Centralized Room migration registry for MeethoPasal PRO.
 * Part 01 establishes schema version 1 (app_settings). Future modules (Parts 02-12)
 * will register non-destructive incremental migrations here.
 */
object DatabaseMigrations {
  const val DATABASE_NAME = "meethopasal_pro.db"
  const val CURRENT_SCHEMA_VERSION = 1

  val ALL_MIGRATIONS: Array<Migration> = emptyArray()
}
