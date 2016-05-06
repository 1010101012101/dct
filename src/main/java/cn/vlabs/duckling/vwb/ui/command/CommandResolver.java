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

package cn.vlabs.duckling.vwb.ui.command;

import java.util.HashMap;

/**
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class CommandResolver {
	private static final Command[] ALL_COMMANDS = new Command[] {
			DPageCommand.VIEW, DPageCommand.EDIT, DPageCommand.DIFF,
			DPageCommand.SHARE, DPageCommand.INFO, DPageCommand.PREVIEW,
			DPageCommand.CONFLICT, VWBCommand.NONE, VWBCommand.ATTACH,
			VWBCommand.ERROR, VWBCommand.EDIT_PREFERENCE, VWBCommand.FIND,
			VWBCommand.CREATE_RESOURCE, VWBCommand.EDIT_PROFILE,
			VWBCommand.LOGIN,VWBCommand.LOGOUT, VWBCommand.ADMIN, VWBCommand.PLAIN,
			PortalCommand.VIEW,PortalCommand.SIMPLE, PortalCommand.CONFIG };

	private static final HashMap<String, Command> m_cmdMap;
	static {
		m_cmdMap = new HashMap<String, Command>();
		Command[] allcommands = ALL_COMMANDS;
		for (Command command : allcommands) {
			m_cmdMap.put(command.getAction(), command);
		}
	}

	public static Command findCommand(String action) {
		return m_cmdMap.get(action);
	}
}
