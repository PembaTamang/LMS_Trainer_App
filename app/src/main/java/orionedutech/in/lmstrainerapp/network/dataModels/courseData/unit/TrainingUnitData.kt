package orionedutech.`in`.lmstrainerapp.network.dataModels.courseData.unit

import orionedutech.`in`.lmstrainerapp.network.dataModels.courseData.unit.TrainingChapterUnitData
import orionedutech.`in`.lmstrainerapp.network.dataModels.courseData.unit.TrainingIndividualUnitData

data class TrainingUnitData(
    val course_links_to: String,
    val trainer_unique: String,
    val training_chapter_data: TrainingChapterUnitData,
    val training_chapter_intro_video: Any,
    val training_chapter_name: String,
    val training_chapter_recall_video: Any,
    val training_chapter_summary_video: Any,
    val training_id: String,
    val training_sub_units_data: Any,
    val training_units_data: List<TrainingIndividualUnitData>
)