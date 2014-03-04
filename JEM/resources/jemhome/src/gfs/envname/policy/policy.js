RESULT.setParallelJobs(1);

/**
 * Extracts the OS type
 */
var osName = SYSINFO.getSystemProperties().getProperty('os.name');

if (osName.contains('Windows')){
	RESULT.getAffinities().add("Windows");
} else if (osName.contains('Linux')){
	RESULT.getAffinities().add("Linux");
} else if (osName.contains('Mac')){
	RESULT.getAffinities().add("MacOS");
} else if (osName.contains('Solaris')){
	RESULT.getAffinities().add("Solaris");
} else if (osName.contains('HP')){
	RESULT.getAffinities().add("HPUX");
} else if (osName.contains('AIX')){
	RESULT.getAffinities().add("AIX");	
} else {
	RESULT.getAffinities().add("AnyOS");
}

var hostnames = SYSINFO.getNetworkProperties().getProperty('hostnames');
if (hostnames != null)
	RESULT.getAffinities().add(hostnames);

/**
 * Computes 80% of free memory, in slot of 128MB
 */
var freemem = SYSINFO.getRuntimeProperties().getProperty('freeMemory');
freemem = freemem / 1024 / 1024 * 0.80;
RESULT.setMemory(Math.min(Math.max(Math.floor(freemem / 64) * 64, 64), 1024));

