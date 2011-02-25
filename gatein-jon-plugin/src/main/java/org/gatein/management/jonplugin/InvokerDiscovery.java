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

package org.gatein.management.jonplugin;

import org.gatein.management.ManagedPortletInvoker;
import org.gatein.management.Portal;
import org.gatein.management.PortalServer;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class InvokerDiscovery implements ResourceDiscoveryComponent
{
   private final Logger log = LoggerFactory.getLogger(this.getClass());

   public Set discoverResources(ResourceDiscoveryContext discoveryContext) throws InvalidPluginConfigurationException, Exception
   {
      // retrieve the portlet invokers associated with the current Portal
      String parentKeyAsString = discoveryContext.getParentResourceContext().getResourceKey();
      ResourceKey parentKey = ResourceKey.parse(parentKeyAsString);
      Map<String, ManagedPortletInvoker> invokers = PortalServer.getPortalManagement(parentKey.getPortalKey()).getManagedPortletInvokers();


      if (invokers.isEmpty())
      {
         return Collections.emptySet();
      }

      Set<DiscoveredResourceDetails> discoveredResources = new HashSet<DiscoveredResourceDetails>(invokers.size());

      for (String invokerName : invokers.keySet())
      {

         DiscoveredResourceDetails detail = new DiscoveredResourceDetails(discoveryContext.getResourceType(), ResourceKey.asString(ResourceKey.getKeyForChild(parentKey, invokerName)),
            invokerName, "N/A", "Monitoring of Portlet Invoker " + invokerName, null, null);


         // Add to return values
         discoveredResources.add(detail);
         log.info("Discovered new portlet invoker: " + invokerName);
      }

      return discoveredResources;
   }
}
