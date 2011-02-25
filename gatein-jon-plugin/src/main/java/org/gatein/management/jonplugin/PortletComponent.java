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
import org.gatein.pc.management.LocalPortletManagementMBean;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;

import java.util.Set;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class PortletComponent implements ResourceComponent, MeasurementFacet
{
   private LocalPortletManagementMBean portlet;

   public void start(ResourceContext resourceContext) throws InvalidPluginConfigurationException, Exception
   {
      InvokerComponent invoker = (InvokerComponent)resourceContext.getParentResourceComponent();
      portlet = invoker.getInvoker().getRegisteredPortlets().get(resourceContext.getResourceKey());
   }

   public void stop()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public AvailabilityType getAvailability()
   {
      return AvailabilityType.UP;
   }

   public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> metrics) throws Exception
   {
      for (MeasurementScheduleRequest req : metrics)
      {
         if (req.getName().equals("actionErrorCount"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, (double)portlet.getActionErrorCount());
            report.addData(res);
         }
         else if (req.getName().equals("actionRequestCount"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, (double)portlet.getActionRequestCount());
            report.addData(res);
         }
         else if (req.getName().equals("averageActionTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, (double)portlet.getAverageActionTime());
            report.addData(res);
         }
         else if (req.getName().equals("averageRenderTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, (double)portlet.getAverageRenderTime());
            report.addData(res);
         }
         else if (req.getName().equals("maxActionTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, (double)portlet.getMaxActionTime());
            report.addData(res);
         }
         else if (req.getName().equals("maxRenderTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, (double)portlet.getMaxRenderTime());
            report.addData(res);
         }
         else if (req.getName().equals("renderErrorCount"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, (double)portlet.getRenderErrorCount());
            report.addData(res);
         }
         else if (req.getName().equals("renderRequestCount"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, (double)portlet.getRenderRequestCount());
            report.addData(res);
         }
      }
   }
}
