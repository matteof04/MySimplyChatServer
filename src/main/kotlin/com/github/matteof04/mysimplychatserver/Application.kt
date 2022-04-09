/*
 * Copyright (C) 2022 Matteo Franceschini <matteof5730@gmail.com>
 *
 * This file is part of mysimplychatserver.
 * mysimplychatserver is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * mysimplychatserver is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with mysimplychatserver.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.matteof04.mysimplychatserver

import com.github.matteof04.mysimplychatserver.commands.CommandHandler
import com.github.matteof04.mysimplychatserver.commands.commands
import com.github.matteof04.mysimplychatserver.connection.Connection
import com.github.matteof04.mysimplychatserver.connection.ConnectionsRegistry
import com.github.matteof04.mysimplychatserver.data.Users
import com.github.matteof04.mysimplychatserver.services.mySimplyChatServerModule
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    println("""
        MySimplyChatServer  Copyright (C) 2022  Matteo Franceschini
        This program comes with ABSOLUTELY NO WARRANTY.
        This is free software, and you are welcome to redistribute it
        under GNU AGPLv3.
    """.trimIndent())
    val config = HikariConfig("/hikari.properties")
    Database.connect(HikariDataSource(config))
    transaction {
        SchemaUtils.create(Users)
    }
    install(WebSockets)
    install(Koin){
        modules(mySimplyChatServerModule)
    }
    routing {
        val connectionsRegistry: ConnectionsRegistry by inject()
        val commandHandler = CommandHandler()
        commands.forEach { commandHandler.registerCommand(it) }
        webSocket("/chat") {
            val thisConnection = Connection(this)
            connectionsRegistry.addConnection(thisConnection)
            try {
                send("<=== CONNECTED ===>")
                for (frame in incoming) {
                    when(frame.frameType){
                        FrameType.TEXT -> {
                            when(thisConnection.loggedIn){
                                true -> {
                                    val receivedText = (frame as Frame.Text).readText()
                                    val textWithUsername = "[${thisConnection.username}]: $receivedText"
                                    val whisperSession = thisConnection.whisperSession
                                    if(whisperSession != null){
                                        whisperSession.send(textWithUsername)
                                        thisConnection.whisperSession = null
                                    }else{
                                        connectionsRegistry.getConnections().forEach {
                                            if(it.loggedIn) {
                                                it.session.send(textWithUsername)
                                            }
                                        }
                                    }
                                }
                                false -> {
                                    send("You must login first!")
                                }
                            }
                        }
                        FrameType.BINARY -> commandHandler.processCommand(thisConnection, (frame as Frame.Binary).readBytes())
                        else -> continue
                    }

                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                connectionsRegistry.removeConnection(thisConnection)
            }
        }
    }
}
