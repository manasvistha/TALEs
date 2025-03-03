package com.example.tales

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddNoteActivity : AppCompatActivity() {

    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var saveNoteButton: FloatingActionButton
    private lateinit var backButton: ImageView

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)
        saveNoteButton = findViewById(R.id.saveNoteButton)
        backButton = findViewById(R.id.backButton)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference.child("notes")

        saveNoteButton.setOnClickListener {
            saveNoteToFirebase()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun saveNoteToFirebase() {
        val title = noteTitle.text.toString().trim()
        val content = noteContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val noteId = database.push().key ?: return
        val note = Note (noteId, title, content)

        database.child(noteId).setValue(note).addOnSuccessListener {
            Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
        }
    }
}