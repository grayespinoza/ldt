plugins {
  id("dev.kikugie.stonecutter")
  id("fabric-loom") version "1.11-SNAPSHOT" apply false
}

stonecutter active "1.21.8"

stonecutter parameters
  {
    swaps["mod_version"] = "\"" + property("mod_version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod_id") != "template"
    dependencies["fapi"] = node.project.property("fabric_version") as String
  }
