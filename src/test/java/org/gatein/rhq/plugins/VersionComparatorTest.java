package org.gatein.rhq.plugins;

import org.junit.Assert;
import org.junit.Test;

public class VersionComparatorTest
{
   @Test
   public void test()
   {
      ensureNFE("6.2.GA");

      test("1", "1.0", 0);
      test("1", "1.0.0", 0);
      test("1", "1.0.0.GA", 0);

      test("1", "2", -1);
      test("9", "11", -1);
      test("1.0.0.DR1", "1.0.0.DR2", -1);
      test("1.0.0.DR10", "1.0.0.ER2", -1);

      ensureNFE("1.GA");
      ensureNFE("1.0.GA");
      ensureNFE("1-GA");

      ensureIAE("-1");
      ensureIAE("1.-1");
      ensureIAE("1.1.-1");

   }

   private void ensureNFE(String v)
   {
      try
      {
         VersionComparator.instance().compare(v, v);
         Assert.fail("NumberFormatException expected");
      } catch (NumberFormatException expected)
      {
      }
   }

   private void ensureIAE(String v)
   {
      try
      {
         VersionComparator.instance().compare(v, v);
         Assert.fail("IllegalArgumentException expected");
      } catch (IllegalArgumentException expected)
      {
      }
   }

   private static void test(String v1, String v2, int expected)
   {
      Assert.assertEquals("'"+ v1 + "' ? '"+ v2 +"'", expected, VersionComparator.instance().compare(v1, v2));
      Assert.assertEquals("'"+ v2 + "' ? '"+ v1 +"'", -expected, VersionComparator.instance().compare(v2, v1));
   }

}
