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

package com.github.matteof04.mysimplychatserver.commands.chat

import com.github.matteof04.mysimplychatserver.commands.Command
import com.github.matteof04.mysimplychatserver.connection.Connection
import com.github.matteof04.mysimplychatserver.connection.ConnectionsRegistry
import com.github.matteof04.mysimplychatserver.services.UserService
import io.ktor.http.cio.websocket.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Ban : Command, KoinComponent {
    override fun getCommandByte(): Byte = 0x04.toByte()

    override suspend fun execute(connection: Connection, payload: String) {
        if(connection.loggedIn){
            val userService: UserService by inject()
            val connectionsRegistry: ConnectionsRegistry by inject()
            val user = userService.getUsersByUsername(payload)
            if(userService.getUsersByUsername(connection.username)!!.admin && user != null){
                userService.banUser(user)
                connection.session.send("User banned with username: $payload")
                connectionsRegistry.getConnections().forEach {
                    if(it.username == payload){
                        it.session.send("<=== BANNED ===>")
                        it.session.close(CloseReason(CloseReason.Codes.NORMAL, "Banned user. Goodbye."))
                    }
                }
            }else{
                connection.session.send("Operation not permitted!")
            }
        }else{
            connection.session.send("Log in first!")
        }
    }
}