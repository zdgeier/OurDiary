package com.zdgeier.ourdiary

import com.google.firebase.Timestamp
import java.util.*

class DiaryEntry(
    var time: Timestamp = Timestamp.now(),
    var text: String = "",
    var location: String = "",
    var image : String = "")