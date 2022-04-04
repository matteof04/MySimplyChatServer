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
import com.github.matteof04.mysimplychatserver.services.UserService
import io.ktor.http.cio.websocket.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SetUsername: Command, KoinComponent {
    override fun getCommandByte(): Byte = 0x01.toByte()

    override suspend fun execute(connection: Connection, payload: String) {
        if(!connection.loggedIn){
            val userService: UserService by inject()
            val user = userService.getUsersByUsername(payload)
            if(user != null){
                if(!user.banned){
                    connection.username = payload
                    connection.session.send("Username accepted: $payload")
                }else{
                    connection.session.send("Operation not permitted!")
                    connection.session.close(CloseReason(CloseReason.Codes.NORMAL, "User banned. Goodbye."))
                }
            }else{
                connection.session.send("Username not accepted")
            }
        }else{
            connection.session.send("Already logged in!")
        }
    }
}