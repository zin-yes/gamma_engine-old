package zin.gammaEngine.core.utils;

public class Logger
{

	private static String PREFIX_ERR = "[GAMMA | ERROR] ";
	private static String PREFIX_LOG = "[GAMMA | LOG] ";
	private static String PREFIX_WRN = "[GAMMA | WARN] ";

	public static void log(String msg)
	{
		System.out.println(PREFIX_LOG + msg);
	}

	public static void warn(String msg)
	{
		System.out.println(PREFIX_WRN + msg);
	}

	public static void error(String msg)
	{
		System.err.println(PREFIX_ERR + msg);
	}

	public static String getErrorPrefix()
	{
		return PREFIX_ERR;
	}

	public static void setErrorPrefix(String prefix)
	{
		PREFIX_ERR = prefix;
	}

	public static String getLogPrefix()
	{
		return PREFIX_LOG;
	}

	public static void setLogPrefxi(String prefix)
	{
		PREFIX_LOG = prefix;
	}

	public static String getWarnPrefix()
	{
		return PREFIX_WRN;
	}

	public static void setWarnPrefix(String prefix)
	{
		PREFIX_WRN = prefix;
	}

}
