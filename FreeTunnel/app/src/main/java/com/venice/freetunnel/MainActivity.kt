package com.venice.freetunnel

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.util.Properties
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private val executor = Executors.newSingleThreadExecutor()
    private var session: Session? = null
    private lateinit var status: TextView
    private lateinit var host: EditText
    private lateinit var port: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var connectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(7, 12, 25)
        window.navigationBarColor = Color.rgb(7, 12, 25)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(26), dp(24), dp(24))
            setBackgroundColor(Color.rgb(7, 12, 25))
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val title = TextView(this).apply {
            text = "FreeTunnel"
            textSize = 32f
            setTextColor(Color.rgb(95, 230, 255))
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "SSH connection test"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }
        content.addView(title, lp())
        content.addView(subtitle, lp())
        status = TextView(this).apply {
            text = "Enter SSH server details"
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(255, 203, 90))
            setPadding(0, dp(22), 0, dp(16))
        }
        content.addView(status, lp())
        host = field("SSH host, e.g. ssh.example.com")
        port = field("SSH port (default 22)", "22")
        username = field("SSH username")
        password = field("SSH password")
        password.inputType = 129
        content.addView(host, lp())
        content.addView(port, lp())
        content.addView(username, lp())
        content.addView(password, lp())
        connectButton = Button(this).apply {
            text = "TEST SSH CONNECTION"
            setOnClickListener { connect() }
        }
        content.addView(connectButton, lp())
        val note = TextView(this).apply {
            text = "This build tests an SSH login only. It does not yet route your phone's traffic through a VPN, and it cannot provide free or unlimited mobile data."
            textSize = 12f
            setTextColor(Color.LTGRAY)
            setPadding(0, dp(20), 0, 0)
            gravity = Gravity.CENTER
        }
        content.addView(note, lp())
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun field(hintText: String, value: String = ""): EditText =
        EditText(this).apply {
            hint = hintText
            setSingleLine(true)
            setText(value)
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.rgb(95, 230, 255))
        }

    private fun lp() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply { bottomMargin = dp(10) }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun connect() {
        val server = host.text.toString().trim()
        val user = username.text.toString().trim()
        val secret = password.text.toString()
        val portNumber = port.text.toString().toIntOrNull() ?: 22
        if (server.isBlank() || user.isBlank() || secret.isBlank() || portNumber !in 1..65535) {
            Toast.makeText(this, "Enter host, valid port, username and password", Toast.LENGTH_SHORT).show()
            return
        }
        connectButton.isEnabled = false
        status.text = "Connecting securely…"
        status.setTextColor(Color.rgb(255, 203, 90))
        executor.execute {
            var newSession: Session? = null
            try {
                val jsch = JSch()
                newSession = jsch.getSession(user, server, portNumber)
                newSession.setPassword(secret)
                val config = Properties()
                config.setProperty("StrictHostKeyChecking", "yes")
                newSession.setConfig(config)
                newSession.timeout = 15000
                newSession.connect(15000)
                session?.disconnect()
                session = newSession
                runOnUiThread {
                    status.text = "SSH connected. VPN routing is not enabled in this build."
                    status.setTextColor(Color.rgb(95, 230, 150))
                    connectButton.text = "DISCONNECT"
                    connectButton.isEnabled = true
                    connectButton.setOnClickListener { disconnect() }
                }
            } catch (e: Exception) {
                try { newSession?.disconnect() } catch (_: Exception) {}
                runOnUiThread {
                    status.text = "Connection failed: ${e.message ?: "check server details"}"
                    status.setTextColor(Color.rgb(255, 100, 110))
                    connectButton.isEnabled = true
                }
            }
        }
    }

    private fun disconnect() {
        executor.execute {
            try { session?.disconnect() } catch (_: Exception) {}
            session = null
            runOnUiThread {
                status.text = "Disconnected"
                status.setTextColor(Color.rgb(255, 203, 90))
                connectButton.text = "TEST SSH CONNECTION"
                connectButton.isEnabled = true
                connectButton.setOnClickListener { connect() }
            }
        }
    }

    override fun onDestroy() {
        try { session?.disconnect() } catch (_: Exception) {}
        executor.shutdownNow()
        super.onDestroy()
    }
}
