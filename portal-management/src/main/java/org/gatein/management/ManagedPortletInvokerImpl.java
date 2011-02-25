/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
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

package org.gatein.management;

import org.gatein.pc.api.InvokerUnavailableException;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.federation.FederatedPortletInvoker;
import org.gatein.pc.management.LocalPortletManagementMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class ManagedPortletInvokerImpl implements ManagedPortletInvoker
{
   private final Logger log = LoggerFactory.getLogger(this.getClass());

   private FederatedPortletInvoker invoker;
   private Map<String, LocalPortletManagementMBean> registeredPortlets = new HashMap<String, LocalPortletManagementMBean>();
   private boolean available = true;

   public ManagedPortletInvokerImpl(FederatedPortletInvoker invoker)
   {
      this.invoker = invoker;
   }

   public boolean isWSRP()
   {
      return PortletInvoker.LOCAL_PORTLET_INVOKER_ID.equals(invoker.getId());
   }

   public boolean isAvailable()
   {
      return available;
   }

   public Map<String, LocalPortletManagementMBean> getRegisteredPortlets()
   {
      return registeredPortlets;
   }

   public int getPortletNumber()
   {
      return registeredPortlets.size();
   }

   public void refreshPortlets()
   {
      Set<Portlet> portlets = Collections.emptySet();
      try
      {
         portlets = invoker.getPortlets();
         available = true;
      }
      catch (PortletInvokerException e)
      {
         if (e instanceof InvokerUnavailableException)
         {
            available = false;
         }
         log.info("Couldn't access portlets from invoker " + invoker.getId() + ". Cause: " + e.getLocalizedMessage());
      }

      // remove portlets that have been undeployed...
      Set<String> previous = new HashSet<String>(registeredPortlets.keySet());
      previous.removeAll(portlets);
      for (String portlet : previous)
      {
         PortalServer.unregisterPortlet(portlet);
         registeredPortlets.remove(portlet);
      }

      for (Portlet portlet : portlets)
      {
         String portletId = portlet.getContext().getId();

         if (!registeredPortlets.containsKey(portletId))
         {
            registeredPortlets.put(portletId, PortalServer.registerPortlet(portlet));
         }
      }
   }
}
