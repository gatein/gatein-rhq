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


import org.exoplatform.portal.application.PortalStatisticService;
import org.gatein.pc.federation.FederatingPortletInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Chris Laprun
 * @version $Revision: 8805 $
 */
public class PortalImpl implements Portal
{
   private PortalKey key;
   private PortalStatisticService statisticService;
   private FederatingPortletInvoker invoker;

   public PortalImpl(PortalKey key)
   {
      this.key = key;
   }

   public PortalKey getKey()
   {
      return key;
   }

   private String getPortalName()
   {
      return key.getPortalName();
   }

   public double getThroughput()
   {
      return statisticService.getThroughput(getPortalName());
   }

   public double getMinExecutionTime()
   {
      return statisticService.getMinTime(getPortalName());
   }

   public double getMaxExecutionTime()
   {
      return statisticService.getMaxTime(getPortalName());
   }

   public double getAverageExecutionTime()
   {
      return statisticService.getAverageTime(getPortalName());
   }

   public Map<String, ManagedPortletInvoker> getManagedPortletInvokers()
   {
      return PortalServer.getManagedPortletInvokersFor(invoker);
   }

   public void setPortalStatisticService(PortalStatisticService statisticService)
   {
      this.statisticService = statisticService;
   }

   public void setInvoker(FederatingPortletInvoker invoker)
   {
      this.invoker = invoker;
   }

   public int compareTo(Portal o)
   {
      return key.compareTo(o.getKey());
   }
}
