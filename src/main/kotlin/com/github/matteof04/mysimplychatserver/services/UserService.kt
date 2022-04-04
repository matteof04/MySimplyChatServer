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

package com.github.matteof04.mysimplychatserver.services

import com.github.matteof04.mysimplychatserver.data.User
import com.github.matteof04.mysimplychatserver.data.Users
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun getUsersByUsername(username: String) = transaction {
        User.find { Users.username eq username }.toList().firstOrNull()
    }
    fun banUser(user: User) = transaction {
        user.banned = true
    }
    fun unbanUser(user: User) = transaction {
        user.banned = false
    }
    fun createUser(newUsername: String, newPassword: String) = transaction {
        User.new {
            username = newUsername
            password = newPassword
        }
    }
}