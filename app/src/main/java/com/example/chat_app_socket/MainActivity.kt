package com.example.chat_app_socket

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app_socket.Adapter.ChatAdapter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button

    private val messages = mutableListOf<String>()
    private lateinit var adapter: ChatAdapter

    private var socket: Socket? = null
    private var out: PrintWriter? = null
    private var input: BufferedReader? = null

    private val SERVER_IP = " 192.168.204.79" // Use actual IP for device.
    private val SERVER_PORT = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        adapter = ChatAdapter(messages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter


        sendButton.setOnClickListener {
            val msg = messageInput.text.toString()
            if (msg.isNotEmpty()) {
                out?.println(msg)
                addMessage("Me: $msg")
                messageInput.text.clear()
            }
        }
        Thread { setupSocket() }.start()
        }

    private fun setupSocket() {
        try {
            socket = Socket(SERVER_IP, SERVER_PORT)
            out = PrintWriter(socket!!.getOutputStream(), true)
            input = BufferedReader(InputStreamReader(socket!!.getInputStream()))

            var line: String?
            while (input!!.readLine().also { line = it } != null) {
                runOnUiThread {
                    addMessage("Friend: $line")
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun addMessage(message: String) {
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
        chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    }
