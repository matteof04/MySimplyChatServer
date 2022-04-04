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

package com.github.matteof04.mysimplychatserver.commands

import com.github.matteof04.mysimplychatserver.connection.Connection

class CommandHandler {
    private val registry = mutableListOf<Command>()
    fun registerCommand(command: Command) = registry.add(command)
    suspend fun processCommand(connection: Connection, rawInput: ByteArray){
        val command = rawInput.first()
        val payload = rawInput.decodeToString(1)
        registry.forEach {
            if(it.getCommandByte() == command){
                it.execute(connection, payload)
            }
        }
    }
}