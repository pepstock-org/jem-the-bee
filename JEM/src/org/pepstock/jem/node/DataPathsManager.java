/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.sgm.DataPaths;
import org.pepstock.jem.node.sgm.DataSetPattern;
import org.pepstock.jem.node.sgm.DataSetRules;
import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.Path;
import org.pepstock.jem.node.sgm.PathsContainer;

import com.thoughtworks.xstream.XStream;

/**
 * Manages all data paths, set in configuration file, and all datasets rules defined for allocation and accessing.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public final class DataPathsManager implements Serializable{
	
	public static final String DEFAULT_RULES_FILE_NAME = "$jem_Default_Datasets_Rules.xml";
	
	public static final String DEFAULT_REG_EX_FOR_ALL = ".*";
	
	private static final long serialVersionUID = 1L;
	
	private DataPaths dataPaths = null;

	private final Map<Pattern, PathsContainer> DATASET_RULES = new LinkedHashMap<Pattern, PathsContainer>();
	
	private File datasetRulesFile = null;
	
	private transient XStream xs = new XStream();

	/**
	 * Empty constructor
	 */
    public DataPathsManager() {
		xs.alias(ConfigKeys.RULES_ALIAS, DataSetRules.class);
		xs.addImplicitCollection(DataSetRules.class, ConfigKeys.PATTERNS_ALIAS);
		xs.processAnnotations(DataSetPattern.class);
    }

	/**
	 * @param dataPaths the storageGroups to set
	 * @throws ConfigurationException 
	 */
	void setDataPaths(DataPaths dataPaths) throws ConfigurationException {
		this.dataPaths = dataPaths;

		for (Path p: dataPaths.getPaths()){
			File dataPath = new File(p.getContent());
			if (!dataPath.exists()) {
				throw new ConfigurationException(NodeMessage.JEMC099E.toMessage().getFormattedMessage(dataPath));
			} else if (!dataPath.isDirectory()) {
				throw new ConfigurationException(NodeMessage.JEMC098E.toMessage().getFormattedMessage(dataPath));
			}
		}
	}

	/**
	 * 
	 * @param fileNameDatasetRules
	 * @return
	 * @throws MessageException
	 * @throws FileNotFoundException
	 */
	DatasetsRulesResult testRules(String fileNameDatasetRules) throws MessageException{
    	File fileDatasetRules = new File(fileNameDatasetRules);
    	DatasetsRulesResult rules = null;
    	DataSetRules dsr;
		try {
			dsr = loadXMLDataSetRules(fileDatasetRules);
			rules = loadRules(dsr);
			if (rules.getRules().isEmpty()){
				// TODO messaggio
				rules.getWarnings().add("No rule has been loaded!");
			}
		} catch (Exception e) {
			// TODO messaggio
			e.printStackTrace();
		}
		return rules;
	}
	/**
	 * Loads rules 
	 * @param fileNameDatasetRules
	 * @throws MessageException
	 * @throws FileNotFoundException
	 */
	void loadRules(File fileDatasetRules) throws MessageException{
		this.datasetRulesFile = fileDatasetRules;
    	DataSetRules dsr;
		try {
			dsr = loadXMLDataSetRules(fileDatasetRules);
			DatasetsRulesResult rules = loadRules(dsr);
			if (rules.getRules().isEmpty()){
				// TODO messaggio
				System.err.println("*** rules are empty");
			} else {
				DATASET_RULES.clear();
				DATASET_RULES.putAll(rules.getRules());
			}
			if (!rules.getWarnings().isEmpty()){
				for (String warning : rules.getWarnings()){
					// TODO messaggio
					System.err.println("*** warning: "+warning);
				}
			}
		} catch (Exception e) {
			// TODO messaggio
			e.printStackTrace();
		}
    	LogAppl.getInstance().emit(NodeMessage.JEMC252I, DATASET_RULES.size());
    	
    }
    /**
     * 
     * @param fileDatasetRules
     * @return
     * @throws Exception
     */
    private DataSetRules loadXMLDataSetRules(File fileDatasetRules) throws Exception{
		FileInputStream fis = new FileInputStream(fileDatasetRules);
		try {
			return (DataSetRules) xs.fromXML(fis);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
		}
    }
    
    /**
     * 
     * @param fileDatasetRules
     * @throws Exception
     */
    void saveXMLDataSetRules(File fileDatasetRules){
		DataSetRules rules = new DataSetRules();
		DataSetPattern pattern = new DataSetPattern();
		pattern.setPathName(Main.DATA_PATHS_MANAGER.getDataPathsNames().get(0));
		pattern.getPatterns().add(DEFAULT_REG_EX_FOR_ALL);
		rules.getPatterns().add(pattern);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileDatasetRules);
			xs.toXML(rules, fos);
		} catch (FileNotFoundException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
		} finally {
			try {
				if (fos != null){
					fos.close();
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
		}
    }

    /**
     * 
     * @param dsr
     * @return
     */
    private DatasetsRulesResult loadRules(DataSetRules dsr){
    	DatasetsRulesResult result = new DatasetsRulesResult();
    	Map<Pattern, PathsContainer> newRules = new LinkedHashMap<Pattern, PathsContainer>();
    	List<String> warnings = new ArrayList<String>();
    	result.setRules(newRules);
    	result.setWarnings(warnings);
    	
		for (DataSetPattern dsPattern : dsr.getPatterns()){
			for (String pattern : dsPattern.getPatterns()){
				Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
				String name = dsPattern.getPathName();
				if (name != null){
					Path mp = getPath(name);
					if (mp != null){
						PathsContainer mps = new PathsContainer();
						mps.setCurrent(mp);
						if (dsPattern.getOldPathName() != null && dsPattern.getOldPathName().trim().length() > 0){
							Path old = getPath(dsPattern.getOldPathName());
							mps.setOld(old);
							if (old == null){
								addWarningMessage(warnings, "Path '"+dsPattern.getOldPathName()+"' is undefined!");
							}
						}
						newRules.put(p, mps);
					} else {
						addWarningMessage(warnings, "Path '"+name+"' is undefined!");
					}
				} else {
					addWarningMessage(warnings, "'pathName' is a mandatory attribute!");
				}
			}
		}
		return result;
    }
    /**
     * 
     * @param warnings
     * @param msg
     */
    private void addWarningMessage(List<String> warnings, String msg){
		if (!warnings.contains(msg)){
			warnings.add(msg);
		}
    }
    
	/**
	 * 
	 * @param name
	 * @return
	 */
    private Path getPath(String name){
    	for (Path mp : dataPaths.getPaths()){
    		if (name.equals(mp.getName())){
    			return mp;
    		}
    	}
    	return null;
    }

	/**
	 * @return the datasetRulesFile
	 */
	public File getDatasetRulesFile() {
		return datasetRulesFile;
	}

	/**
     * 
     * @param fileName
     * @return
	 * @throws InvalidDatasetNameException 
     */
    public PathsContainer getPaths(String fileName) throws InvalidDatasetNameException{
    	if (fileName != null){
    		for (Entry<Pattern, PathsContainer> entry : DATASET_RULES.entrySet()){
    			if (entry.getKey().matcher(fileName).matches()){
    				return entry.getValue();
    			}
    		}
    	}
    	throw new InvalidDatasetNameException(fileName);
    }
    
    /**
     * 
     * @param fileName
     * @return
     */
    public String getAbsoluteDataPath(String fileName){
    	for (Path mp : dataPaths.getPaths()){
    		String pathToCheck = FilenameUtils.normalize(mp.getContent().endsWith(File.separator) ? mp.getContent() : mp.getContent()+File.separator, true);
    		if (StringUtils.startsWithIgnoreCase(fileName, pathToCheck)){
    			return mp.getContent();
    		}
    	}
    	return null;
    }
    
    public List<String> getDataPathsNames(){
    	List<String> groups = new LinkedList<String>();
    	for (Path mp : dataPaths.getPaths()){
    		groups.add(mp.getName());
    	}
    	return groups;
    }
    
    public List<String> getDataPaths(){
    	List<String> groups = new LinkedList<String>();
    	for (Path mp : dataPaths.getPaths()){
    		groups.add(FilenameUtils.normalize(mp.getContent(), true));
    	}
    	return groups;
    }
}
