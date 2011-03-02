/*
 * JBoss, a division of Red Hat
 * Copyright 2011, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.management.jmx;

import org.mc4j.ems.connection.EmsConnection;
import org.mc4j.ems.connection.bean.EmsBean;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.plugins.jmx.JMXComponent;
import org.rhq.plugins.jmx.MBeanResourceDiscoveryComponent;

import java.util.Set;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public abstract class GateInJMXResourceDiscovery extends MBeanResourceDiscoveryComponent<JMXComponent> implements GateInJMXResource
{
   @Override
   public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext<JMXComponent> context)
   {
      Set<DiscoveredResourceDetails> resources = super.discoverResources(context);

      EmsConnection connection = context.getParentResourceComponent().getEmsConnection();
      for (DiscoveredResourceDetails resource : resources)
      {
         String resourceKey = resource.getResourceKey();
         EmsBean statisticBean = connection.getBean(resourceKey);
         Configuration configuration = context.getDefaultPluginConfiguration();
         String portalContainerName = configuration.get("objectName").getConfiguration().getSimpleValue("container", "portal");

         String[] names = (String[])statisticBean.getAttribute(getAttributeName()).getValue();
         for (String name : names)
         {
            DiscoveredResourceDetails detail = createResourceDetail(context, portalContainerName, name);

            resources.add(detail);
         }

         resources.remove(resource);
      }

      return resources;
   }

   protected abstract DiscoveredResourceDetails createResourceDetail(ResourceDiscoveryContext<JMXComponent> context, String portalContainerName, String name);
}
