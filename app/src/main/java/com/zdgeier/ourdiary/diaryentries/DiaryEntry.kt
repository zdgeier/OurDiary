package com.zdgeier.ourdiary.diaryentries

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.text.SimpleDateFormat

class DiaryEntry(
    var time: Timestamp = Timestamp.now(),
    var text: String = "",
    var location: GeoPoint = GeoPoint(0.0, 0.0),
    var image : String = ""
) {
    fun getTimeDisplay() =
        SimpleDateFormat.getDateTimeInstance().format(time.toDate())

    fun getTextDisplay() =
            text

    fun getLocationDisplay() =
            location.toString()
}