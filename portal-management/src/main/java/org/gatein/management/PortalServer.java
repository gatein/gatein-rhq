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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalStatisticService;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.federation.FederatedPortletInvoker;
import org.gatein.pc.federation.FederatingPortletInvoker;
import org.gatein.pc.management.LocalPortletManagement;
import org.gatein.pc.management.LocalPortletManagementMBean;
import org.gatein.pc.management.PortletContainerManagementInterceptorImpl;
import org.jboss.mx.util.MBeanProxy;
import org.jboss.mx.util.MBeanServerLocator;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class PortalServer
{
   private static final Log log = LogFactory.getLog(PortalServer.class);

   private static Map<Portal.PortalKey, Portal> portals = new ConcurrentHashMap<Portal.PortalKey, Portal>();
   private static MBeanServer server = MBeanServerLocator.locateJBoss();
   private static final PortletContainerManagementInterceptorImpl interceptor = new PortletContainerManagementInterceptorImpl();
   public static final String PORTLET_JMX_PREFIX = "gatein.management:service=management,type=portlet,name=";

   private PortalServer()
   {
   }

   /*static
   {
      try
      {
         Set<ObjectName> objectNames = server.queryNames(createObjectName("exo:container=portal,name=*,portal=*"), null);
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
      }
   }*/

   public static Portal createAndAddPortal(final String portalContainerName, final String portalName, final PortalStatisticService portalStatisticService, final FederatingPortletInvoker federatingPortletInvoker)
   {
      Portal.PortalKey portalKey = new Portal.PortalKey(portalContainerName, portalName);
      Portal portal = new PortalImpl(portalKey);
      portal.setPortalStatisticService(portalStatisticService);
      portal.setInvoker(federatingPortletInvoker);
      portals.put(portalKey, portal);
      return portal;
   }

   public static Map<String, ManagedPortletInvoker> getManagedPortletInvokersFor(FederatingPortletInvoker federatingPortletInvoker)
   {
      Collection<FederatedPortletInvoker> federatedInvokers = federatingPortletInvoker.getFederatedInvokers();

      Map<String, ManagedPortletInvoker> invokers = new HashMap<String, ManagedPortletInvoker>(federatedInvokers.size());
      for (FederatedPortletInvoker invoker : federatedInvokers)
      {
         org.gatein.pc.portlet.PortletInvokerInterceptor portletInvoker = (org.gatein.pc.portlet.PortletInvokerInterceptor)invoker.getPortletInvoker();
         portletInvoker.setNext(interceptor);

         invokers.put(invoker.getId(), new ManagedPortletInvokerImpl(invoker));
      }

      return Collections.unmodifiableMap(invokers);
   }

   static LocalPortletManagementMBean registerPortlet(Portlet portlet)
   {
      String portletId = portlet.getContext().getId();

      LocalPortletManagementMBean management = new LocalPortletManagement(portlet, interceptor);

      // attempt to register MBean with server
      try
      {
         server.registerMBean(management, getMBeanName(portletId));

         log.debug("Registered Management MBean for: " + portletId);
      }
      catch (Exception e)
      {
         log.info("Couldn't register " + portletId + " portlet. Cause: " + e.getLocalizedMessage(), e);
      }

      return management;
   }

   static void unregisterPortlet(String portlet)
   {
      try
      {
         server.unregisterMBean(getMBeanName(portlet));
      }
      catch (Exception e)
      {
         log.info("Couldn't unregister MBean associated with portlet " + portlet + ". Cause: " + e.getLocalizedMessage(), e);
      }
   }

   private static ObjectName getMBeanName(String id) throws MalformedObjectNameException
   {
      return new ObjectName(PORTLET_JMX_PREFIX + id);
   }

   public static boolean isRunning()
   {
      return !portals.isEmpty();
   }

   private static ObjectName createObjectName(String name)
   {
      ObjectName objecName;
      try
      {
         objecName = new ObjectName(name);
      }
      catch (MalformedObjectNameException e)
      {
         throw new IllegalArgumentException("'" + name + "' is not a valid ObjectName");
      }
      return objecName;
   }

   public static Portal getPortalManagement(Portal.PortalKey name)
   {
      return portals.get(name);
   }

   /**
    * Retrieves the MBeanProxy associated with the given class and name from the specified MBeanServer.
    *
    * @param expectedClass the expected class of the MBean's proxy
    * @param name          the MBean's ObjectName
    * @param server        the MBeanServer from which to retrieve the MBeanProxy
    * @return a MBeanProxy for the specified MBean if it exists
    * @throws RuntimeException if the MBean couldn't be retrieved
    */
   private static <T> T getMBeanProxy(Class<T> expectedClass, ObjectName name, MBeanServer server)
   {
      try
      {
         return expectedClass.cast(MBeanProxy.get(expectedClass, name, server));
      }
      catch (Exception e)
      {
         String message = "Couldn't retrieve '" + name.getCanonicalName() + "' MBean with class " + expectedClass.getName();
         log.error(message, e);
         throw new RuntimeException(message, e);
      }
   }

   public static List<Portal> getPortals()
   {
      ArrayList<Portal> portalList = new ArrayList<Portal>(portals.values());
      Collections.sort(portalList);
      return portalList;
   }
}
