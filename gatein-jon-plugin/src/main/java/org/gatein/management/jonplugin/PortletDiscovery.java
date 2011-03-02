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

package org.gatein.management.jonplugin;

import org.gatein.management.ResourceKey;
import org.gatein.management.jmx.GateInJMXResourceDiscovery;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.plugins.jmx.JMXComponent;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class PortletDiscovery extends GateInJMXResourceDiscovery
{
   @Override
   protected DiscoveredResourceDetails createResourceDetail(ResourceDiscoveryContext<JMXComponent> context, String portalContainerName, String name)
   {
      String parentKey = context.getParentResourceContext().getResourceKey();
      ResourceKey parent = ResourceKey.parse(parentKey);

      // ideally we would monitor invokers as well
      ResourceKey invoker = ResourceKey.getKeyForChild(parent, "local");
      ResourceKey key = ResourceKey.getKeyForChild(invoker, name);


      return new DiscoveredResourceDetails(context.getResourceType(), ResourceKey.asString(key),
         key.getPortletId(), "version", "Monitoring of GateIn portlet '" + name + "' running in " + key.getPortalKey(), null, null);
   }

   @Override
   public String getAttributeName()
   {
      return "ApplicationList";
   }
}
