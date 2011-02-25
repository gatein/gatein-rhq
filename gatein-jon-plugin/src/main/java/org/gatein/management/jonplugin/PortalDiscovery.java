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

import org.gatein.management.Portal;
import org.gatein.management.PortalServer;
import org.gatein.management.PortalStatisticService;
import org.gatein.management.jmx.JMXPortalStatisticService;
import org.mc4j.ems.connection.EmsConnection;
import org.mc4j.ems.connection.bean.EmsBean;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.plugins.jmx.JMXComponent;
import org.rhq.plugins.jmx.MBeanResourceDiscoveryComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


public class PortalDiscovery extends MBeanResourceDiscoveryComponent<JMXComponent>
{
   private static final Logger log = LoggerFactory.getLogger(PortalDiscovery.class);


   @Override
   public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext<JMXComponent> context)
   {
      Set<DiscoveredResourceDetails> resources = super.discoverResources(context);

      EmsConnection connection = context.getParentResourceComponent().getEmsConnection();
      for (DiscoveredResourceDetails resource : resources)
      {
         String resourceKey = resource.getResourceKey();
         EmsBean portalBean = connection.getBean(resourceKey);
         String portalContainerName = portalBean.getBeanName().getKeyProperty("portal").replace('"', ' ').trim();

         String[] portalNames = (String[])portalBean.getAttribute("PortalList").getValue();
         for (String portalName : portalNames)
         {
            PortalStatisticService statisticService = new JMXPortalStatisticService(portalBean, portalName);
            Portal portal = PortalServer.createAndAddPortal(portalContainerName, portalName, statisticService);
            Portal.PortalKey key = portal.getKey();
            DiscoveredResourceDetails detail = new DiscoveredResourceDetails(context.getResourceType(), ResourceKey.createPortalKeyFrom(key),
               key.getPortalName(), "version", "Monitoring of GateIn resources", null, null);

            resources.add(detail);
            log.info("Discovered new Portal");
         }

      }

      return resources;
   }
}