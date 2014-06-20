package org.gatein.rhq.plugins;

import org.junit.Test;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.resource.ResourceType;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryCallback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
public class PortalDiscoveryCallbackTest
{
   @Test
   public void testVersionLowerThan611() throws Exception
   {
      Configuration configuration = new Configuration();
      ResourceType type = new ResourceType();

      DiscoveredResourceDetails details = new DiscoveredResourceDetails(type, "bogus", "JPP Something", "6.0.1.GA", "", configuration, null);
      ResourceDiscoveryCallback.DiscoveryCallbackResults results = new PortalDiscoveryCallback().discoveredResources(details);

      assertEquals(ResourceDiscoveryCallback.DiscoveryCallbackResults.UNPROCESSED, results);
      assertNull(details.getPluginConfiguration().getSimpleValue("expectedRuntimeProductName"));
   }

   @Test
   public void testVersionIs611() throws Exception
   {
      Configuration configuration = new Configuration();
      ResourceType type = new ResourceType();

      DiscoveredResourceDetails details = new DiscoveredResourceDetails(type, "bogus", "JPP Something", "6.1.1.GA", "", configuration, null);
      ResourceDiscoveryCallback.DiscoveryCallbackResults results = new PortalDiscoveryCallback().discoveredResources(details);

      assertEquals(ResourceDiscoveryCallback.DiscoveryCallbackResults.PROCESSED, results);
      assertEquals("Portal", details.getPluginConfiguration().getSimpleValue("expectedRuntimeProductName"));
   }

   @Test
   public void testVersionHigherThan611() throws Exception
   {
      Configuration configuration = new Configuration();
      ResourceType type = new ResourceType();

      DiscoveredResourceDetails details = new DiscoveredResourceDetails(type, "bogus", "JPP Something", "6.2.0.ER3", "", configuration, null);
      ResourceDiscoveryCallback.DiscoveryCallbackResults results = new PortalDiscoveryCallback().discoveredResources(details);

      assertEquals(ResourceDiscoveryCallback.DiscoveryCallbackResults.PROCESSED, results);
      assertEquals("Portal", details.getPluginConfiguration().getSimpleValue("expectedRuntimeProductName"));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testFailOnUnknownVersion() throws Exception
   {
      Configuration configuration = new Configuration();
      ResourceType type = new ResourceType();

      DiscoveredResourceDetails details = new DiscoveredResourceDetails(type, "bogus", "JPP Something", "6.22.0.ER3", "", configuration, null);
      new PortalDiscoveryCallback().discoveredResources(details);
   }

}
