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
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.configuration.ConfigurationFacet;
import org.rhq.core.pluginapi.configuration.ConfigurationUpdateReport;
import org.rhq.core.pluginapi.event.EventContext;
import org.rhq.core.pluginapi.inventory.CreateChildResourceFacet;
import org.rhq.core.pluginapi.inventory.CreateResourceReport;
import org.rhq.core.pluginapi.inventory.DeleteResourceFacet;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.pluginapi.operation.OperationContext;
import org.rhq.core.pluginapi.operation.OperationFacet;
import org.rhq.core.pluginapi.operation.OperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


public class PortalComponent implements ResourceComponent, MeasurementFacet, OperationFacet, ConfigurationFacet,
   CreateChildResourceFacet, DeleteResourceFacet
{
   private final Logger log = LoggerFactory.getLogger(this.getClass());


   public static final String DUMMY_EVENT = "portal-jopr-pluginDummyEvent"; // Same as in Plugin-Descriptor

   EventContext eventContext;

   private Portal portal;

   /**
    * Return availability of this resource
    *
    * @see org.rhq.core.pluginapi.inventory.ResourceComponent#getAvailability()
    */
   public AvailabilityType getAvailability()
   {
      return AvailabilityType.UP;
   }


   /**
    * Start the resource connection
    *
    * @see org.rhq.core.pluginapi.inventory.ResourceComponent#start(org.rhq.core.pluginapi.inventory.ResourceContext)
    */
   public void start(ResourceContext context) throws InvalidPluginConfigurationException, Exception
   {

      Configuration conf = context.getPluginConfiguration();

      portal = PortalServer.getPortalManagement(Portal.PortalKey.parse(context.getResourceKey()));

      /*eventContext = context.getEventContext();
     PortalEventPoller eventPoller = new PortalEventPoller();
     eventContext.registerEventPoller(eventPoller, 60);*/

   }


   /**
    * Tear down the rescource connection
    *
    * @see org.rhq.core.pluginapi.inventory.ResourceComponent#stop()
    */
   public void stop()
   {
//      eventContext.unregisterEventPoller(DUMMY_EVENT);
   }


   /**
    * Gather measurement data
    *
    * @see org.rhq.core.pluginapi.measurement.MeasurementFacet#getValues(org.rhq.core.domain.measurement.MeasurementReport,
    *      java.util.Set)
    */
   public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> metrics) throws Exception
   {
      for (MeasurementScheduleRequest req : metrics)
      {
         String name = req.getName();
         if (name.equals("averageExecutionTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, portal.getAverageExecutionTime());
            report.addData(res);
         }
         else if (name.equals("minExecutionTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, portal.getMinExecutionTime());
            report.addData(res);
         }
         else if (name.equals("maxExecutionTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, portal.getMaxExecutionTime());
            report.addData(res);
         }
         else if (name.equals("throughput"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, portal.getThroughput());
            report.addData(res);
         }

      }
   }


   public void startOperationFacet(OperationContext context)
   {

   }


   /**
    * Invokes the passed operation on the managed resource
    *
    * @param name   Name of the operation
    * @param params The method parameters
    * @returns An operation result
    */
   public OperationResult invokeOperation(String name, Configuration params) throws Exception
   {
      OperationResult res = new OperationResult();
      if ("refreshPortletList".equals(name))
      {
         System.out.println("refreshPortletList called res = " + res);
      }
      return res;

   }


   /**
    * Load the configuration from a resource into the configuration
    *
    * @return The configuration of the resource
    * @see org.rhq.core.pluginapi.configuration.ConfigurationFacet
    */
   public Configuration loadResourceConfiguration()
   {
      // TODO supply code to load the configuration from the resource into the plugin
      return null;
   }

   /**
    * Write down the passed configuration into the resource
    *
    * @param report The configuration updated by the server
    * @see org.rhq.core.pluginapi.configuration.ConfigurationFacet
    */
   public void updateResourceConfiguration(ConfigurationUpdateReport report)
   {
      // TODO supply code to update the passed report into the resource
   }

   /**
    * Create a child resource
    *
    * @see org.rhq.core.pluginapi.inventory.CreateChildResourceFacet
    */
   public CreateResourceReport createResource(CreateResourceReport report)
   {
      // TODO supply code to create a child resource

      return null; // TODO change this
   }

   /**
    * Delete a child resource
    *
    * @see org.rhq.core.pluginapi.inventory.DeleteResourceFacet
    */
   public void deleteResource() throws Exception
   {
      // TODO supply code to delete a child resource
   }
}