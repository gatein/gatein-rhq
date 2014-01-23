package org.gatein.rhq.plugins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryCallback;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class PortalDiscoveryCallback implements ResourceDiscoveryCallback
{

   private static final Log log = LogFactory.getLog("org.gatein.rhq.plugins");

   @Override
   public DiscoveryCallbackResults discoveredResources(DiscoveredResourceDetails discoveredResourceDetails) throws Exception
   {
      String version = discoveredResourceDetails.getResourceVersion();
      if (version.contains("6.1.1"))
      {
         discoveredResourceDetails.getPluginConfiguration().setSimpleValue("expectedRuntimeProductName", "Portal");
         return DiscoveryCallbackResults.PROCESSED;
      } else {
         return DiscoveryCallbackResults.UNPROCESSED;
      }
   }
}
