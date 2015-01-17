/**
 * Extracts the OS type
 */

RESULT.parallelJobs = 1; 
 
def osName = SYSINFO.getSystemProperties().get("os.name");

if (osName.contains("Windows")){
	RESULT.affinities.add("windows");	
} else if (osName.contains("Linux")){
	RESULT.affinities.add("linux");
} else if (osName.contains("Mac")){
	RESULT.affinities.add("macos");
} else if (osName.contains("Solaris")){
	RESULT.affinities.add("solaris");
} else if (osName.contains("HP")){
	RESULT.affinities.add("hpux");
} else if (osName.contains("AIX")){
	RESULT.affinities.add("aix");	
} else { 
	RESULT.affinities.add("anyos");
}

def hostnames = SYSINFO.getNetworkProperties().get("hostnames");

if (hostnames != null){
	items = hostnames.split(',')
	items.each{ 
		if (it.indexOf('.') > -1){
			names = it.split('\\.');
			shortHostname = names[0];
			RESULT.affinities.add(shortHostname);			
		} else {
			RESULT.affinities.add(it);
		}
	}
}

/**
 * Computes 80% of free memory, in slot of 128MB
 */
 
def freemem = SYSINFO.getRuntimeProperties().get("freeMemory").toLong();
freemem = freemem / 1024 / 1024 * 0.80; 
RESULT.memory = Math.min(Math.max(Math.floor(freemem / 64) * 64, 64), 1024);

return