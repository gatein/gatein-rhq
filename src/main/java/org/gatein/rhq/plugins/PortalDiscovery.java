/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.gatein.rhq.plugins;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.resource.ResourceType;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.plugins.jmx.JMXComponent;
import org.rhq.plugins.jmx.MBeanResourceDiscoveryComponent;

import java.util.Set;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class PortalDiscovery extends MBeanResourceDiscoveryComponent<JMXComponent<?>>
{
   @Override
   public Set<DiscoveredResourceDetails> performDiscovery(Configuration pluginConfiguration, JMXComponent parentResourceComponent, ResourceType resourceType, boolean skipUnknownProps)
   {
      Set<DiscoveredResourceDetails> details = super.performDiscovery(pluginConfiguration, parentResourceComponent, resourceType, skipUnknownProps);
      for (DiscoveredResourceDetails detail : details)
      {
         // Replace quotes since eXo adds them around the portal container in the JMX object name.
         String container = detail.getResourceName().replaceAll("\"", "");

         detail.setResourceName(container);
         detail.setResourceKey(container);
      }

      return details;
   }
}
