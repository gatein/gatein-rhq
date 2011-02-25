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

import junit.framework.TestCase;
import org.gatein.management.Portal;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class ResourceKeyTestCase extends TestCase
{
   public void testRoundtripParsing()
   {
      Portal.PortalKey portalKey = new Portal.PortalKey("container", "portal");
      ResourceKey key = new ResourceKey(portalKey, "invoker", "portlet");
      ResourceKey parsed = ResourceKey.parse(ResourceKey.asString(key));
      assertEquals(key, parsed);
      assertEquals(portalKey, parsed.getPortalKey());
      assertEquals("invoker", parsed.getInvokerId());
      assertEquals("portlet", parsed.getPortletId());

      key = new ResourceKey(portalKey, "invoker", null);
      parsed = ResourceKey.parse(ResourceKey.asString(key));
      assertEquals(key, parsed);

      key = new ResourceKey(portalKey, null, null);
      parsed = ResourceKey.parse(ResourceKey.asString(key));
      assertEquals(key, parsed);

      key = new ResourceKey(portalKey, null, "portlet");
      parsed = ResourceKey.parse(ResourceKey.asString(key));
      assertEquals(key, parsed);
      assertEquals(portalKey, parsed.getPortalKey());
      assertNull(parsed.getInvokerId());
      assertNull(parsed.getPortletId());
   }

   public void testGetChildFor()
   {
      Portal.PortalKey portalKey = new Portal.PortalKey("container", "portal");

      ResourceKey key = new ResourceKey(portalKey, null, null);
      ResourceKey child = ResourceKey.getKeyForChild(key, "invoker");
      assertEquals(new ResourceKey(portalKey, "invoker", null), child);

      assertEquals(new ResourceKey(portalKey, "invoker", "portlet"), ResourceKey.getKeyForChild(child, "portlet"));
   }

   public void testAResourceKeyShouldAlwaysHaveAPortalKey()
   {
      try
      {
         new ResourceKey(null, null, null);
         fail("A ResourceKey should always have at least a PortalKey");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }
   }

   public void testExtractShouldFailOnNull()
   {
      try
      {
         ResourceKey.extractPortalKeyFrom(null);
         fail("should fail on null");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }
   }

   public void testGetChildForShouldFailOnNullParent()
   {
      try
      {
         ResourceKey.getKeyForChild(null, null);
         fail("should fail on null");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }
   }

   public void testGetChildForShouldFailOnNullChildId()
   {
      try
      {
         ResourceKey.getKeyForChild(new ResourceKey(new Portal.PortalKey("container", "portal"), null, null), null);
         fail("should fail on null");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }
   }
}
