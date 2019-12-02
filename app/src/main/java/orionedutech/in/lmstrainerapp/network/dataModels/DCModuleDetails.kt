package orionedutech.`in`.lmstrainerapp.network.dataModels

data class DCModuleDetails(
    val module_chapters: List<DCModuleChapter>,
    val module_id: String,
    val module_links_to: String,
    val module_links_to_verb: String,
    val module_name: String,
    val module_sub_units: String,
    val module_units: String
)