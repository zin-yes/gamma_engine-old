package zin.gammaEngine.core.utils;

public class Logger
{

	private static String PREFIX_INFO = "[GAMMA | INFO] ";
	private static String PREFIX_WRN = "\u001B[30m\u001B[43m[GAMMA | WARN] ";
	private static String PREFIX_ERR = "\u001B[30m\u001B[41m[GAMMA | ERROR] ";

	public static void info(String msg)
	{
		System.out.println(PREFIX_INFO + msg);
	}

	public static void warn(String msg)
	{
		System.out.println(PREFIX_WRN + msg + "\u001B[0m");
	}

	public static void error(String msg)
	{
		System.err.println(PREFIX_ERR + msg + "\u001B[0m");
	}

	public static String getInfoPrefix()
	{
		return PREFIX_INFO;
	}

	public static void setInfoPrefix(String prefix)
	{
		PREFIX_INFO = prefix;
	}

	public static String getWarnPrefix()
	{
		return PREFIX_WRN;
	}

	public static void setWarnPrefix(String prefix)
	{
		PREFIX_WRN = prefix;
	}

	public static String getErrorPrefix()
	{
		return PREFIX_ERR;
	}

	public static void setErrorPrefix(String prefix)
	{
		PREFIX_ERR = prefix;
	}

}
