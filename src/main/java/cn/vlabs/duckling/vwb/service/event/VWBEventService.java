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

package cn.vlabs.duckling.vwb.service.event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * Introduction Here.
 * 
 * @date 2010-2-23
 * @author euniverse
 */
public class VWBEventService {
	private static final class VWBEventDelegate {
		private ArrayList<WeakReference<VWBEventListener>> m_listenerList = new ArrayList<WeakReference<VWBEventListener>>();

		public boolean addVWBEventListener(VWBEventListener listener) {
			synchronized (m_listenerList) {
				return m_listenerList.add(new WeakReference<VWBEventListener>(
						listener));
			}
		}

		/**
		 * Notify all listeners having a registered interest in change events of
		 * the supplied WikiEvent.
		 */
		public void fireEvent(VWBEvent event) {
			boolean needsCleanup = false;

			try {
				synchronized (m_listenerList) {
					for (int i = 0; i < m_listenerList.size(); i++) {
						VWBEventListener listener = m_listenerList.get(i).get();
						if (listener != null) {
							listener.actionPerformed(event);
						} else {
							needsCleanup = true;
						}
					}
					//
					// Remove all such listeners which have expired
					//
					if (needsCleanup) {
						for (int i = 0; i < m_listenerList.size(); i++) {
							WeakReference<VWBEventListener> w = m_listenerList
									.get(i);

							if (w.get() == null)
								m_listenerList.remove(i--);
						}
					}

				}
			} catch (ConcurrentModificationException e) {
				log.info(
						"Concurrent modification of event list; please report this.",
						e);
			}

		}

		public Set<VWBEventListener> getVWBEventListeners() {
			synchronized (m_listenerList) {
				TreeSet<VWBEventListener> set = new TreeSet<VWBEventListener>(
						new VWBEventListenerComparator());

				for (Iterator<WeakReference<VWBEventListener>> i = m_listenerList
						.iterator(); i.hasNext();) {
					VWBEventListener l = i.next().get();
					if (l != null) {
						set.add(l);
					}
				}
				return Collections.unmodifiableSet(set);
			}
		}

		public boolean isListening() {
			synchronized (m_listenerList) {
				return !m_listenerList.isEmpty();
			}
		}

		public boolean removeVWBEventListener(VWBEventListener listener) {
			synchronized (m_listenerList) {
				for (Iterator<WeakReference<VWBEventListener>> i = m_listenerList
						.iterator(); i.hasNext();) {
					VWBEventListener l = i.next().get();
					if (l == listener) {
						i.remove();
						return true;
					}
				}
			}

			return false;
		}
	}

	private static class VWBEventListenerComparator implements
			Comparator<VWBEventListener> {
		public int compare(VWBEventListener listener1,
				VWBEventListener listener2) {
			VWBEventListener w1 = (VWBEventListener) listener1;
			VWBEventListener w2 = (VWBEventListener) listener2;
			if (w1 == w2 || w1.equals(w2))
				return 0;
			return w1.hashCode() - w1.hashCode();
		}
	}

	/* Singleton instance of the VwbEventManager. */
	private static final VWBEventService c_instance = new VWBEventService();

	/* Optional listener to be used as all-event monitor. */
	private static VWBEventListener c_monitor = null;

	/* If true, permits a VwbEventMonitor to be set. */
	private static boolean c_permitMonitor = false;

	private static final Logger log = Logger.getLogger(VWBEventService.class);

	public static final boolean addVWBEventListener(Object client,
			VWBEventListener listener) {
		if (client == VWBEventService.class) {
			if (c_permitMonitor)
				c_monitor = listener;
			return c_permitMonitor;
		}
		VWBEventDelegate delegate = getInstance().getDelegateFor(client);
		return delegate.addVWBEventListener(listener);
	}

	/**
	 * Notify all listeners of the WikiEventDelegate having a registered
	 * interest in change events of the supplied WikiEvent.
	 * 
	 * @param client
	 *            the client initiating the event.
	 * @param event
	 *            the VwbEvent to fire.
	 */
	public static void fireEvent(Object client, VWBEvent event) {
		VWBEventDelegate source = getInstance().getDelegateFor(client);
		if (source != null)
			source.fireEvent(event);
		if (c_monitor != null)
			c_monitor.actionPerformed(event);
	}

	public static final VWBEventService getInstance() {
		return c_instance;
	}

	public static final Set<VWBEventListener> getVWBEventListeners(Object client)
			throws UnsupportedOperationException {
		VWBEventDelegate delegate = getInstance().getDelegateFor(client);
		return delegate.getVWBEventListeners();
	}

	/**
	 * Returns true if there are one or more listeners registered with the
	 * provided client Object (undelegated event source). This locates any
	 * delegate and checks to see if it has any listeners attached.
	 * 
	 * @param client
	 *            the client Object
	 * @return True, if there is a listener for this client object.
	 */
	public static boolean isListening(Object client) {
		VWBEventDelegate source = getInstance().getDelegateFor(client);
		return source != null ? source.isListening() : false;
	}

	public static final void removeListeners(Object client) {
		Map<Object, VWBEventDelegate> delegates = getInstance().getDelegates();
		synchronized (delegates) {
			delegates.remove(client);
		}
	}

	public static final boolean removeVWBEventListener(Object client,
			VWBEventListener listener) {
		if (client == VWBEventService.class) {
			c_monitor = null;
			return true;
		}
		VWBEventDelegate delegate = getInstance().getDelegateFor(client);
		return delegate.removeVWBEventListener(listener);
	}

	public static final boolean removeVWBEventListener(VWBEventListener listener) {
		// get the Map.entry object for the entire Map, then check match on
		// entry (listener)
		Map<Object, VWBEventDelegate> sources = getInstance().getDelegates();
		synchronized (sources) {
			// get an iterator over the Map.Enty objects in the map
			Iterator<Map.Entry<Object, VWBEventDelegate>> it = sources
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Object, VWBEventDelegate> entry = it.next();
				// the entry value is the delegate
				VWBEventDelegate delegate = (VWBEventDelegate) entry.getValue();

				// now see if we can remove the listener from the delegate
				// (delegate may be null because this is a weak reference)
				if (delegate != null
						&& delegate.removeVWBEventListener(listener)) {
					return true; // was removed
				}
			}
		}
		return false;
	}

	/* The Map of client object to VwbEventDelegate. */
	private final Map<Object, VWBEventDelegate> m_delegates = new HashMap<Object, VWBEventDelegate>();

	private VWBEventService() {
	}

	private VWBEventDelegate getDelegateFor(Object client) {
		synchronized (m_delegates) {
			VWBEventDelegate delegate = (VWBEventDelegate) m_delegates
					.get(client);
			if (delegate == null) {
				delegate = new VWBEventDelegate();
				m_delegates.put(client, delegate);
			}
			return delegate;
		}
	}

	private Map<Object, VWBEventDelegate> getDelegates() {
		return m_delegates;
	}
}
