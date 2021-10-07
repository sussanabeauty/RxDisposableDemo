package org.sussanacode.rxjavadisposableapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.sussanacode.rxjavadisposableapplication.databinding.ActivityMainBinding
import org.sussanacode.rxjavadisposableapplication.entity.Note
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var  disposable: CompositeDisposable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        disposable = CompositeDisposable()

        // add to Composite observable
        // .map() operator is used to turn the note into all uppercase letters
        // add to Composite observable
        // .map() operator is used to turn the note into all uppercase letters
        disposable.add(getNotesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(object : Function<Note?, Note?> {
                        @Throws(Exception::class)
                        override fun apply(note: Note): Note? {
                            // Making the note to all uppercase
                            note.note.uppercase()
                            //note.setNote(note.getNote().toUpperCase())
                            return note
                        }
                    })
                .subscribeWith(getNotesObserver())
        )


    }

    private fun getNotesObserver(): DisposableObserver<Note> {

        return object:DisposableObserver<Note>(){
            override fun onNext(note: Note) {
                Log.d("Note: ", note.note);
            }

            override fun onError(e: Throwable) {
                e.message?.let {
                    Log.d("onError: " , it);
                }
            }

            override fun onComplete() {
                Log.d("onComplete: ", "note are emitted successfully");
            }
        }
    }

    private fun getNotesObservable(): Observable<Note> {
        val notes : ArrayList<Note> = prepareNote()

        return Observable.create { emitter ->
            notes.forEach {
                if (!emitter.isDisposed)
                    emitter.onNext(it)
            }

            if (!emitter.isDisposed)
                emitter.onComplete()
        }

    }

    private fun prepareNote(): ArrayList<Note> {

        val notes: ArrayList<Note> = ArrayList()
        notes.add(Note(1, "Cream for ice-cream!"))
        notes.add(Note(2, "Read study notes"))
        notes.add(Note(3, "buy gym clothes!"))
        notes.add(Note(4, "pay phone bill!"))

        return notes

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}

