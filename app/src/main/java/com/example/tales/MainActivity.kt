package com.example.tales

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private lateinit var fabAddNote: FloatingActionButton
    private val notesList = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        fabAddNote = findViewById(R.id.fabAddTask)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("notes")

        // Set up RecyclerView with Grid Layout
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = NoteAdapter(notesList, ::deleteNote, ::editNote) // ✅ Fix: Pass functions
        recyclerView.adapter = adapter

        // Load notes from Firebase
        loadNotesFromFirebase()

        // Add new note button
        fabAddNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadNotesFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    if (note != null) {
                        notesList.add(note)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load notes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ✅ Function to delete a note
    private fun deleteNote(note: Note) {
        database.child(note.id).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
            loadNotesFromFirebase()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Function to edit a note
    private fun editNote(note: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("noteId", note.id)
        intent.putExtra("title", note.title)
        intent.putExtra("content", note.content)
        startActivity(intent)
    }
}
