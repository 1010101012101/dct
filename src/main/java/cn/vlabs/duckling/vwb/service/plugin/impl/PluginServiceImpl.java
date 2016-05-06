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

package cn.vlabs.duckling.vwb.service.plugin.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.ecs.xhtml.b;
import org.apache.ecs.xhtml.div;
import org.apache.ecs.xhtml.li;
import org.apache.ecs.xhtml.pre;
import org.apache.ecs.xhtml.ul;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.util.ClassUtil;
import cn.vlabs.duckling.util.FileUtil;
import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.DPagePluginInfo;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;
import cn.vlabs.duckling.vwb.service.plugin.PluginService;

/**
 * Introduction Here.
 * 
 * @date 2010-2-24
 * @author Fred Zhang (fred@cnic.cn)
 */
public class PluginServiceImpl implements PluginService {
	private static final String DEFAULT_PACKAGE = PluginServiceImpl.class.getPackage().getName()+".plugins";
	private static Logger log = Logger.getLogger(PluginServiceImpl.class);
	private static final String PARAM_BODY = "_body";
	/**
	 * The name of the command line content parameter. The value is "_cmdline".
	 */
	private static final String PARAM_CMDLINE = "_cmdline";
	private static final String PARAM_DEBUG = "debug";
	/**
	 * Keeps a list of all known plugin classes.
	 */
	private Map<String, DPagePluginInfo> m_pluginClassMap = new HashMap<String, DPagePluginInfo>();
	private boolean m_pluginsEnabled = true;
	private List<String> m_searchPath = new ArrayList<String>();
	private String pluginSearchPath;
	/**
	 * Attempts to locate a plugin class from the class path set in the property
	 * file.
	 * 
	 * @param classname
	 *            Either a fully fledged class name, or just the name of the
	 *            file (that is,
	 *            "cn.vlabs.duckling.dct.services.plugin.impl.Counter" or just
	 *            plain "Counter").
	 * 
	 * @return A found class.
	 * 
	 * @throws ClassNotFoundException
	 *             if no such class exists.
	 */
	private Class<?> findPluginClass(String classname)
			throws ClassNotFoundException {
		return ClassUtil.findClass(m_searchPath, classname);
	}
	/**
	 * Register a plugin.
	 */
	private void registerPlugin(DPagePluginInfo pluginClass) {
		String name;

		// Registrar the plugin with the className without the package-part
		name = pluginClass.getClassName();
		if (name != pluginClass.getClassName()) {
			log.debug("Registering plugin [name]: " + name);
			m_pluginClassMap.put(name, pluginClass);
		}
	}

	/**
	 * Outputs a HTML-formatted version of a stack trace.
	 */
	private String stackTrace(Map<String, String> params, Throwable t) {
		div d = new div();
		d.setClass("debug");
		d.addElement("Plugin execution failed, stack trace follows:");
		StringWriter out = new StringWriter();
		t.printStackTrace(new PrintWriter(out));
		d.addElement(new pre(out.toString()));
		d.addElement(new b("Parameters to the plugin"));

		ul list = new ul();
		for (String key : params.keySet()) {
			list.addElement(new li(key + "'='" + params.get(key)));
		}

		d.addElement(list);

		return d.toString();
	}

	/**
	 * Enables or disables plugin execution.
	 */
	public void enablePlugins(boolean enabled) {
		m_pluginsEnabled = enabled;
	}

	/**
	 * Executes a plugin class in the given context.
	 * 
	 * @param context
	 *            The current VWBContext.
	 * @param classname
	 *            The name of the class. Can also be a shortened version without
	 *            the package name, since the class name is searched from the
	 *            package search path.
	 * 
	 * @param params
	 *            A parsed map of key-value pairs.
	 * 
	 * @return Whatever the plugin returns.
	 * 
	 * @throws PluginException
	 *             If the plugin execution failed for some reason.
	 * 
	 */
	public String execute(VWBContext context, String classname, Map<String,String> params)
			throws PluginException {
		if (!m_pluginsEnabled)
			return "";

		ResourceBundle rb = context
				.getBundle(AbstractDPagePlugin.PLUGINS_RESOURCEBUNDLE);
		Object[] args = { classname };
		try {
			AbstractDPagePlugin plugin;

			boolean debug = TextUtil.isPositive((String) params
					.get(PARAM_DEBUG));

			DPagePluginInfo pluginInfo = (DPagePluginInfo) m_pluginClassMap
					.get(classname);

			if (pluginInfo == null) {
				pluginInfo = DPagePluginInfo
						.newInstance(findPluginClass(classname));
				registerPlugin(pluginInfo);
			}
			try {
				plugin = pluginInfo.newPluginInstance();
				plugin.setSite(context.getSite());
			} catch (InstantiationException e) {
				throw new PluginException(MessageFormat.format(
						rb.getString("plugin.error.cannotinstantiate"), args),
						e);
			} catch (IllegalAccessException e) {
				throw new PluginException(MessageFormat.format(
						rb.getString("plugin.error.notallowed"), args), e);
			} catch (Exception e) {
				throw new PluginException(MessageFormat.format(
						rb.getString("plugin.error.instantationfailed"), args),
						e);
			}

			//
			// ...and launch.
			//
			try {
				return plugin.execute(context, params);
			} catch (PluginException e) {
				if (debug) {
					return stackTrace(params, e);
				}

				// Just pass this exception onward.
				throw (PluginException) e.fillInStackTrace();
			} catch (Throwable t) {
				// But all others get captured here.
				log.info("Plugin failed while executing:", t);
				if (debug) {
					log.debug("Detail exception info:", t);
					return stackTrace(params, t);
				}
				throw new PluginException(
						rb.getString("plugin.error.pluginfailed"), t);
			}

		} catch (ClassNotFoundException e) {
			throw new PluginException(MessageFormat.format(
					rb.getString("plugin.error.couldnotfind"), args), e);
		} catch (ClassCastException e) {
			throw new PluginException(MessageFormat.format(
					rb.getString("plugin.error.notawikiplugin"), args), e);
		}
	}

	public void init() {
		if (pluginSearchPath != null) {
			StringTokenizer tok = new StringTokenizer(pluginSearchPath, ",");
			while (tok.hasMoreTokens()) {
				m_searchPath.add(tok.nextToken().trim());
			}
		}
		m_searchPath.add(DEFAULT_PACKAGE);
	}

	/**
	 * Parses plugin arguments. Handles quotes and all other kewl stuff.
	 * 
	 * <h3>Special parameters</h3> The plugin body is put into a special
	 * parameter defined by {@link #PARAM_BODY}; the plugin's command line into
	 * a parameter defined by {@link #PARAM_CMDLINE}; and the bounds of the
	 * plugin within the wiki page text by a parameter defined by
	 * {@link #PARAM_BOUNDS}, whose value is stored as a two-element int[]
	 * array, i.e., <tt>[start,end]</tt>.
	 * 
	 * @param argstring
	 *            The argument string to the plugin. This is typically a list of
	 *            key-value pairs, using "'" to escape spaces in strings,
	 *            followed by an empty line and then the plugin body. In case
	 *            the parameter is null, will return an empty parameter list.
	 * 
	 * @return A parsed list of parameters.
	 * 
	 * @throws IOException
	 *             If the parsing fails.
	 */
	public Map<String,String> parseArgs(String argstring) throws IOException {
		HashMap<String,String> arglist = new HashMap<String,String>();

		//
		// Protection against funny users.
		//
		if (argstring == null)
			return arglist;

		arglist.put(PARAM_CMDLINE, argstring);

		StringReader in = new StringReader(argstring);
		StreamTokenizer tok = new StreamTokenizer(in);
		int type;

		String param = null;
		String value = null;

		tok.eolIsSignificant(true);

		boolean potentialEmptyLine = false;
		boolean quit = false;

		while (!quit) {
			String s;

			type = tok.nextToken();

			switch (type) {
			case StreamTokenizer.TT_EOF:
				quit = true;
				s = null;
				break;

			case StreamTokenizer.TT_WORD:
				s = tok.sval;
				potentialEmptyLine = false;
				break;

			case StreamTokenizer.TT_EOL:
				quit = potentialEmptyLine;
				potentialEmptyLine = true;
				s = null;
				break;

			case StreamTokenizer.TT_NUMBER:
				s = Integer.toString(new Double(tok.nval).intValue());
				potentialEmptyLine = false;
				break;

			case '\'':
				s = tok.sval;
				break;

			default:
				s = null;
			}

			//
			// Assume that alternate words on the line are
			// parameter and value, respectively.
			//
			if (s != null) {
				if (param == null) {
					param = s;
				} else {
					value = s;
					arglist.put(param, value);
					param = null;
				}
			}
		}

		//
		// Now, we'll check the body.
		//

		if (potentialEmptyLine) {
			StringWriter out = new StringWriter();
			FileUtil.copyContents(in, out);

			String bodyContent = out.toString();

			if (bodyContent != null) {
				arglist.put(PARAM_BODY, bodyContent);
			}
		}

		return arglist;
	}

	/**
	 * Returns plugin execution status. If false, plugins are not executed when
	 * they are encountered on a DPage, and an empty string is returned in their
	 * place.
	 */
	public boolean pluginsEnabled() {
		return m_pluginsEnabled;
	}

	public void setPluginSearchPath(String searchPath){
		this.pluginSearchPath = searchPath;
	}

}
