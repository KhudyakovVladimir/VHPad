package com.khudyakovvladimir.vhcloudnotepad.dependencyinjection

import android.app.Application
import androidx.room.Room
import com.khudyakovvladimir.vhcloudnotepad.model.database.NoteDatabase
import com.khudyakovvladimir.vhcloudnotepad.utils.*
import com.khudyakovvladimir.vhcloudnotepad.view.fragments.*
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [MainModule::class])
interface AppComponent {

    fun injectMainScreen(mainScreen: MainScreen)
    fun injectSingleNote(singleNote: SingleNote)
    fun injectNotification(notification: Notification)
    fun injectShopping(shopping: Shopping)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}

@Module
class MainModule {

    @Provides
    fun provideNoteDatabase(application: Application): NoteDatabase {
        return Room.databaseBuilder(application, NoteDatabase::class.java, "note_db").build()
    }

    @Provides
    fun providesNoteDao(noteDatabase: NoteDatabase) = noteDatabase.noteDao()

    @Provides
    fun provideProductDao(noteDatabase: NoteDatabase) = noteDatabase.productDao()

    @Provides
    fun provideSoundPoolHelper(application: Application): SoundPoolHelper {
        return SoundPoolHelper(application)
    }

    @Provides
    fun provideTimeHelper(): TimeHelper {
        return TimeHelper()
    }

    @Provides
    fun provideTextHelper(): TextHelper {
        return TextHelper()
    }

    @Provides
    fun provideImageHelper(): ImageHelper {
        return ImageHelper()
    }

    @Provides
    fun provideWeatherHelper(): WeatherHelper {
        return WeatherHelper()
    }
}
