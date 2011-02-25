/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/

package org.gatein.management.jonplugin;


import org.rhq.core.domain.event.Event;
import org.rhq.core.pluginapi.event.EventPoller;

import java.util.HashSet;
import java.util.Set;


public class PortalEventPoller implements EventPoller
{

   public PortalEventPoller()
   {

   }


   /**
    * Return the type of events we handle
    *
    * @see org.rhq.core.pluginapi.event.EventPoller#getEventType()
    */
   public String getEventType()
   {
      return PortalComponent.DUMMY_EVENT;
   }


   /**
    * Return collected events
    *
    * @see org.rhq.core.pluginapi.event.EventPoller#poll()
    */
   public Set<Event> poll()
   {
      Set<Event> eventSet = new HashSet<Event>();
      // TODO add your events here. Below is an example that
      /*
      synchronized (events) {
          eventSet.addAll(events);
          events.clear();
      }
      */
      return eventSet;
   }

}