package com.khudyakovvladimir.vhcloudnotepad.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khudyakovvladimir.vhcloudnotepad.repository.NoteRepository
import com.khudyakovvladimir.vhcloudnotepad.repository.ProductRepository
import com.khudyakovvladimir.vhcloudnotepad.utils.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.lang.IllegalArgumentException

class NoteViewModelFactory @AssistedInject constructor(
    @Assisted("application")
    var application: Application,
    var productRepository: ProductRepository,
    var noteRepository: NoteRepository,
    var timeHelper: TimeHelper,
    var soundPoolHelper: SoundPoolHelper,
    var textHelper: TextHelper,
    var imageHelper: ImageHelper,
    var weatherHelper: WeatherHelper
    ):ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(
                application = application,
                productRepository = productRepository,
                noteRepository = noteRepository,
                timeHelper = timeHelper,
                soundPoolHelper = soundPoolHelper,
                textHelper = textHelper,
                imageHelper = imageHelper,
                weatherHelper = weatherHelper
                ) as T
        }
        throw IllegalArgumentException("Unable to construct NoteViewModel")
    }

    @AssistedFactory
    interface Factory {
        fun createNoteViewModelFactory(@Assisted("application")application: Application): NoteViewModelFactory
}
}