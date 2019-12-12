package orionedutech.`in`.lmstrainerapp.model

data class Answers(
    val header : String,
    val question : String,
    val answer : String,
    val correctAnswer : String,
    val status : String,
    val totalQuestions:String,
    val totalcorrect : String,
    val totalincorrect : String,
    val isHeader : Boolean,
    val sl : String,
    val type: Int
)