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

package com.github.matteof04.mysimplychatserver.connection

import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }
    val connectionId = lastId.getAndIncrement()
    var username = ""
    var password = ""
    var loggedIn = false
    var loginFailedAttempt = 0
    var whisperSession: DefaultWebSocketSession? = null
}