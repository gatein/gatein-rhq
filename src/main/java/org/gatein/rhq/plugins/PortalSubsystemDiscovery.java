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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.modules.plugins.jbossas7.BaseComponent;
import org.rhq.modules.plugins.jbossas7.SubsystemDiscovery;

import java.util.Set;

/**
 * Stub class unless we want some special logic. Provides some trace logging for troubleshooting.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class PortalSubsystemDiscovery extends SubsystemDiscovery
{
   private static final Log log = LogFactory.getLog("org.gatein.rhq.plugins");
   @Override
   public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext<BaseComponent<?>> context) throws Exception
   {
      if (log.isTraceEnabled())
      {
         log.trace("Discovering resources for GateIn plugin...");
      }

      Set<DiscoveredResourceDetails> details = super.discoverResources(context);
      if (log.isTraceEnabled())
      {
         for (DiscoveredResourceDetails detail : details)
         {
            log.trace("Successfully discovered resource: " + detail.getResourceKey());
         }
      }

      return details;
   }
}
