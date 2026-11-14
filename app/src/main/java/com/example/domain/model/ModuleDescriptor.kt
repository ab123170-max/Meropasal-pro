package com.example.domain.model

/**
 * Represents a planned module in the 12-part MeethoPasal PRO roadmap.
 * Part 01 marks upcoming modules as unavailable so no nonfunctional actions are exposed.
 */
data class ModuleDescriptor(
  val id: String,
  val title: String,
  val subtitle: String,
  val targetPartLabel: String,
  val isAvailable: Boolean = false,
)

object SuiteRoadmap {
  val plannedModules: List<ModuleDescriptor> = listOf(
    ModuleDescriptor(
      id = "pos_billing",
      title = "POS & Billing",
      subtitle = "Order entry, split bills & receipts",
      targetPartLabel = "Part 02",
    ),
    ModuleDescriptor(
      id = "menu_catalog",
      title = "Menu & Recipes",
      subtitle = "Categories, modifiers & pricing",
      targetPartLabel = "Part 03",
    ),
    ModuleDescriptor(
      id = "table_floor",
      title = "Tables & Floor",
      subtitle = "Dine-in occupancy & reservations",
      targetPartLabel = "Part 04",
    ),
    ModuleDescriptor(
      id = "kds_kitchen",
      title = "Kitchen Display",
      subtitle = "Live prep tickets & station routing",
      targetPartLabel = "Part 05",
    ),
    ModuleDescriptor(
      id = "inventory_stock",
      title = "Inventory Control",
      subtitle = "Ingredients, wastage & par alerts",
      targetPartLabel = "Part 06",
    ),
    ModuleDescriptor(
      id = "reports_ledger",
      title = "Financial Reports",
      subtitle = "Tax summaries & shift closures",
      targetPartLabel = "Part 07+",
    ),
  )
}
