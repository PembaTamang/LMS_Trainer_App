package orionedutech.`in`.lmstrainerapp.network.dataModels.courseData.subunit

data class TrainingSubUnitData(
    val course_links_to: String,
    val trainer_unique: String,
    val training_chapter_data: TrainingChapterSubUnitData,
    val training_chapter_intro_video: Any,
    val training_chapter_name: String,
    val training_chapter_recall_video: Any,
    val training_chapter_summary_video: Any,
    val training_id: String,
    val training_sub_units_data: List<TrainingIndividualSubUnitsData>,
    val training_units_data: Any
)