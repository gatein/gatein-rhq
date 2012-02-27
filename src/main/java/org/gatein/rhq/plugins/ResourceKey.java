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

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class ResourceKey
{
   private static final String SEPARATOR = "@";

   private final String portalContainerName;
   private final String resourceName;
   
   public static ResourceKey create(String portalContainerName, String resourceName)
   {
      if (portalContainerName == null) throw new IllegalArgumentException("portalContainerName cannot be null");
      if (resourceName == null) throw new IllegalArgumentException("resourceName cannot be null");

      return new ResourceKey(portalContainerName, resourceName);
   }
   
   public static ResourceKey from(String compositeKey)
   {
      String[] parts = compositeKey.split(SEPARATOR);
      if (parts.length < 2) throw new IllegalArgumentException("Invalid compositeKey " + compositeKey);

      return create(parts[0], parts[1]);
   }

   private ResourceKey(String portalContainerName, String resourceName)
   {
      this.resourceName = resourceName;
      this.portalContainerName = portalContainerName;
   }

   public String getResourceName()
   {
      return resourceName;
   }

   public String getPortalContainerName()
   {
      return portalContainerName;
   }
   
   public String getDescription()
   {
      return "Resource for '" + resourceName + "' running in portal container '" + portalContainerName +"'";
   }

   @Override
   public String toString()
   {
      return portalContainerName + SEPARATOR + resourceName;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ResourceKey siteKey = (ResourceKey) o;

      if (!portalContainerName.equals(siteKey.portalContainerName)) return false;
      if (!resourceName.equals(siteKey.resourceName)) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = resourceName.hashCode();
      result = 31 * result + portalContainerName.hashCode();
      return result;
   }
}
