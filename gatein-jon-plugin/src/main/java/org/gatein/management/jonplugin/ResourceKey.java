/*
* JBoss, a division of Red Hat
* Copyright 2008, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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

import org.gatein.common.util.ParameterValidation;
import org.gatein.management.Portal;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
class ResourceKey
{
   private final static String SEPARATOR = "/";
   private Portal.PortalKey portalKey;
   private String invokerId;
   private String portletId;

   ResourceKey(Portal.PortalKey portalKey, String invokerId, String portletId)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(portalKey, "PortalKey");
      this.portalKey = portalKey;

      // only take portlet id into account if invoker id is not null or empty
      if (!ParameterValidation.isNullOrEmpty(invokerId))
      {
         this.invokerId = invokerId;
         this.portletId = portletId;
      }

   }

   static ResourceKey parse(String resourceKey)
   {
      String[] split = resourceKey.split(SEPARATOR);
      switch (split.length)
      {
         case 1:
            return new ResourceKey(Portal.PortalKey.parse(split[0]), null, null);
         case 2:
            return new ResourceKey(Portal.PortalKey.parse(split[0]), split[1], null);
         case 3:
            return new ResourceKey(Portal.PortalKey.parse(split[0]), split[1], split[2]);
         default:
            throw new IllegalArgumentException("Invalid ResourceKey: '" + resourceKey + "'");
      }
   }

   static ResourceKey getKeyForChild(ResourceKey parent, String childId)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(parent, "Parent resource");
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(childId, "child identifier", null);

      String invokerId = parent.getInvokerId();
      if (invokerId == null)
      {
         return new ResourceKey(parent.getPortalKey(), childId, null);
      }
      else
      {
         return new ResourceKey(parent.getPortalKey(), invokerId, childId);
      }
   }

   static String asString(ResourceKey key)
   {
      return Portal.PortalKey.compose(key.portalKey) + (key.invokerId != null ? SEPARATOR + key.invokerId + (key.portletId != null ? SEPARATOR + key.portletId : "") : "");
   }

   static Portal.PortalKey extractPortalKeyFrom(String resourceKey)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(resourceKey, "resource key", null);
      return Portal.PortalKey.parse(resourceKey.substring(resourceKey.indexOf(SEPARATOR)));
   }

   public Portal.PortalKey getPortalKey()
   {
      return portalKey;
   }

   public String getInvokerId()
   {
      return invokerId;
   }

   public String getPortletId()
   {
      return portletId;
   }

   @Override
   public String toString()
   {
      return asString(this);
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      ResourceKey that = (ResourceKey)o;

      if (invokerId != null ? !invokerId.equals(that.invokerId) : that.invokerId != null)
      {
         return false;
      }
      if (!portalKey.equals(that.portalKey))
      {
         return false;
      }
      if (portletId != null ? !portletId.equals(that.portletId) : that.portletId != null)
      {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = portalKey.hashCode();
      result = 31 * result + (invokerId != null ? invokerId.hashCode() : 0);
      result = 31 * result + (portletId != null ? portletId.hashCode() : 0);
      return result;
   }
}
