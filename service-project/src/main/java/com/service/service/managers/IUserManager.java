/*
 * Copyright 2013 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.service.service.managers;

import com.service.service.IUserService;

/**
 * @author workhub
 */

public interface IUserManager extends IManager, IUserService {

	/**
	 * Returns true if the username represents an internal account
	 *
	 * @param username a
	 * @return true if the specified username represents an internal account
 	 * @since 1.4.0
	 */
	boolean isInternalAccount(String username);

}