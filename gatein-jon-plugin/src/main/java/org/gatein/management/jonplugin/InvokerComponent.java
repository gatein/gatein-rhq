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
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.pluginapi.operation.OperationFacet;
import org.rhq.core.pluginapi.operation.OperationResult;

import java.util.Set;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class InvokerComponent implements ResourceComponent, MeasurementFacet, OperationFacet
{
   private ManagedPortletInvoker invoker;

   public void start(ResourceContext resourceContext) throws InvalidPluginConfigurationException, Exception
   {
      String keyAsString = resourceContext.getResourceKey();
      ResourceKey key = ResourceKey.parse(keyAsString);
      Portal portal = PortalServer.getPortalManagement(key.getPortalKey());
      String invokerId = key.getInvokerId();
      if(invokerId != null)
      {
         invoker = portal.getManagedPortletInvokers().get(invokerId);
      }
      else
      {
         throw new IllegalArgumentException("'" + keyAsString + "' doesn't properly identify a PortletInvoker.");
      }
   }

   public void stop()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public AvailabilityType getAvailability()
   {
      return invoker.isAvailable() ? AvailabilityType.UP : AvailabilityType.DOWN;
   }

   public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> metrics) throws Exception
   {
      for (MeasurementScheduleRequest req : metrics)
      {
         if (req.getName().equals("portletNumber"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, (double)invoker.getPortletNumber());
            report.addData(res);
         }
      }
   }

   public OperationResult invokeOperation(String name, Configuration configuration) throws InterruptedException, Exception
   {
      OperationResult res = new OperationResult();
      if ("refreshPortletList".equals(name))
      {
         invoker.refreshPortlets();
      }
      return res;
   }

   public ManagedPortletInvoker getInvoker()
   {
      return invoker;
   }
}
