package sairepa;

import java.util.Map;
import java.nio.charset.Charset;

public class CharsetGetter
{
    public static void main(String args[])
    {
      for (Map.Entry charSetName : Charset.availableCharsets().entrySet()) {
	System.out.println(charSetName.getKey());
      }
    }
}
