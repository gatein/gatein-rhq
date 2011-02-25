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

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalStatisticService;
import org.gatein.management.Portal;
import org.gatein.management.PortalServer;
import org.gatein.pc.federation.FederatingPortletInvoker;
import org.mc4j.ems.connection.EmsConnection;
import org.mc4j.ems.connection.bean.EmsBean;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.plugins.jmx.JMXComponent;
import org.rhq.plugins.jmx.MBeanResourceDiscoveryComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/** Discovery class */
//public class PortalDiscovery implements ResourceDiscoveryComponent
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
         String portalContainerName = (String)portalBean.getAttribute("portal").getValue();
         /*PortalContainer container = portalBean.getProxy(PortalContainer.class);

         FederatingPortletInvoker invoker = (FederatingPortletInvoker)container.getComponentInstanceOfType(FederatingPortletInvoker.class);

         PortalStatisticService statisticService = (PortalStatisticService)container.getComponentInstanceOfType(PortalStatisticService.class);
         String[] portalNames = statisticService.getPortalList();

         for (String portalName : portalNames)
         {
            Portal portal = PortalServer.createAndAddPortal(portalContainerName, portalName, statisticService, invoker);
            Portal.PortalKey key = portal.getKey();
            DiscoveredResourceDetails detail = new DiscoveredResourceDetails(context.getResourceType(), ResourceKey.createPortalKeyFrom(key),
               key.getPortalName(), "version", "Monitoring of GateIn resources", null, null);

            resources.add(detail);
            log.info("Discovered new Portal");
         }*/

      }

      return resources;
   }

   /*@Override
   public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext context) throws InvalidPluginConfigurationException, Exception
   {
      Set<DiscoveredResourceDetails> discoveredResources = new HashSet<DiscoveredResourceDetails>();

      List<Portal> portals = PortalServer.getPortals();
      for (Portal portal : portals)
      {

         Portal.PortalKey key = portal.getKey();
         DiscoveredResourceDetails detail = new DiscoveredResourceDetails(context.getResourceType(), ResourceKey.createPortalKeyFrom(key),
            key.getPortalName(), "version", "Monitoring of GateIn resources", null, null);

         discoveredResources.add(detail);
         log.info("Discovered new Portal");
      }

      return discoveredResources;
   }*/

   /*try
   {
      Set<ObjectName> objectNames = jboss.queryNames(createObjectName("exo:container=portal,name=*,portal=*"), null);
      for (ObjectName objectName : objectNames)
      {
         PortalContainer container = getMBeanProxy(PortalContainer.class, objectName, server);
         String portalContainerName = objectName.getKeyProperty("portal");

         PortalStatisticService statisticService = (PortalStatisticService)container.getComponentInstanceOfType(PortalStatisticService.class);
         String[] portalNames = statisticService.getPortalList();

         // Retrieve the portlet invoker to access portlet and add the statistics interceptor
         FederatingPortletInvoker invoker = (FederatingPortletInvoker)container.getComponentInstanceOfType(FederatingPortletInvoker.class);


         for (String portalName : portalNames)
         {
            Portal.PortalKey portalKey = new Portal.PortalKey(portalContainerName, portalName);
            Portal portal = new PortalImpl(portalKey);
            portal.setPortalStatisticService(statisticService);
            portal.setInvoker(invoker);
            portals.put(portalKey, portal);
         }
      }
   }
   catch (Exception e)
   {
      log.info("Failed to start PortalServer: " + e.getLocalizedMessage(), e);
   }*/


      /*Set<DiscoveredResourceDetails> discoveredResources = new HashSet<DiscoveredResourceDetails>();

      List<Portal> portals = PortalServer.getPortals();
      for (Portal portal : portals)
      {

         *//**
          * A discovered resource must have a unique key, that must
          * stay the same when the resource is discovered the next
          * time
          *//*
         Portal.PortalKey key = portal.getKey();
         DiscoveredResourceDetails detail = new DiscoveredResourceDetails(context.getResourceType(), ResourceKey.createPortalKeyFrom(key),
            key.getPortalName(), "version", "Monitoring of GateIn resources", null, null);

         discoveredResources.add(detail);
         log.info("Discovered new Portal");
      }

      return discoveredResources;
      }
      */
}