/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */

package cn.vlabs.duckling.vwb.ui.rsi.api;

import cn.vlabs.rest.ServiceException;

/**
 * Introduction Here.
 * 
 * @date 2010-4-19
 * @author Fred Zhang (fred@cnic.cn)
 */
public class ExceptionUtil {
	public static DCTRsiServiceException changeException(ServiceException se) {
		DCTRsiServiceException exception = null;
		switch (se.getCode()) {
		case DCTRsiErrorCode.EMPTY_ERROR:
			exception = new DPageEmptyException(se.getCode(), se.getMessage());
			break;
		case DCTRsiErrorCode.LOGIN_ERROR:
			exception = new LoginException(se.getCode(), se.getMessage());
			break;
		case DCTRsiErrorCode.FORBIDDEN_ERROR:
			exception = new AccessControlException(se.getCode(), se.getMessage());
			break;
		case DCTRsiErrorCode.CONFLICT_ERROR:
			exception = new ConflictException(se.getCode(), se.getMessage());
			break;
		case DCTRsiErrorCode.PARAMETER_INVALID_ERROR:
			exception = new ParametersInvalidException(se.getCode(), se.getMessage());
			break;
		case DCTRsiErrorCode.SITE_DOMAIN_CONFLICT:
			exception = new SiteDomainConflictException(se.getCode(), se.getMessage());
			break;
		default:
			throw new RuntimeException(se);
		}
		return exception;
	}
}
