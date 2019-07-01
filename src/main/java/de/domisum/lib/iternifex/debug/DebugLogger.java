package de.domisum.lib.iternifex.debug;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.logging.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DebugLogger
{

	// LOG
	public static void log(String message)
	{
		if(DebugSettings.DEBUG_ACTIVE)
		{
			String callerClassName = new Exception().getStackTrace()[1].getClassName();
			Logger logger = java.util.logging.Logger.getLogger(callerClassName);

			logger.info(message);
		}
	}

}
