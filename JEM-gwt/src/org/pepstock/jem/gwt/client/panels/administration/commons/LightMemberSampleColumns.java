package org.pepstock.jem.gwt.client.panels.administration.commons;

import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.stats.LightMemberSample;

import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Contains all sortable common LightMemberSample columns
 * @author Marco "Fuzzo" Cuccato
 */
public final class LightMemberSampleColumns {
	
	

	/**
	 * To avoid any instantiation
	 */
    private LightMemberSampleColumns() {

    }

	/**
	 * PID (unsortable)
	 */
	public static final TextColumn<LightMemberSample> PID = new TextColumn<LightMemberSample>() {
		@Override
		public String getValue(LightMemberSample memberSample) {
			return String.valueOf(memberSample.getPid());
		}
	};

	/**
	 * TIME (unsortable)
	 */
	public static final TextColumn<LightMemberSample> TIME = new TextColumn<LightMemberSample>() {
		@Override
		public String getValue(LightMemberSample memberSample) {
			return memberSample.getTime();
		}
	};

	/**
	 * IP ADDRESS AND PORT
	 */
	public static final TextColumn<LightMemberSample> IP_ADDRESS_AND_PORT_SORTABLE = new TextColumn<LightMemberSample>() {
		@Override
		public String getValue(LightMemberSample object) {
			return object.getMemberLabel() + " - " + object.getMemberHostname();
		}
	};
	static {
		IP_ADDRESS_AND_PORT_SORTABLE.setSortable(true);
	}

	/**
	 * TIME
	 */
	public static final TextColumn<LightMemberSample> TIME_SORTABLE = new TextColumn<LightMemberSample>() {
		@Override
		public String getValue(LightMemberSample memberSample) {
			return memberSample.getTime();
		}
	};
	
	static {
		TIME_SORTABLE.setSortable(true);
	}

	/*
	 * ENTRIES
	 */
	
	/**
	 * INPUT ENTRIES
	 */
	public static final TextColumn<LightMemberSample> INPUT_ENTRIES_SORTABLE = new EntriesSortableColumnByQueue(Queues.INPUT_QUEUE);

	/**
	 * RUNNING ENTRIES
	 */
	public static final TextColumn<LightMemberSample> RUNNING_ENTRIES_SORTABLE = new EntriesSortableColumnByQueue(Queues.RUNNING_QUEUE);

	/**
	 * OUTPUT ENTRIES
	 */
	public static final TextColumn<LightMemberSample> OUTPUT_ENTRIES_SORTABLE = new EntriesSortableColumnByQueue(Queues.OUTPUT_QUEUE);

	/**
	 * ROUTING ENTRIES
	 */
	public static final TextColumn<LightMemberSample> ROUTING_ENTRIES_SORTABLE = new EntriesSortableColumnByQueue(Queues.ROUTING_QUEUE);

	/**
	 * RESOURCES ENTRIES
	 */
	public static final TextColumn<LightMemberSample> RESOURCES_ENTRIES_SORTABLE = new EntriesSortableInternalColumn(Queues.COMMON_RESOURCES_MAP);
	
	/**
	 * ROLES ENTRIES
	 */
	public static final TextColumn<LightMemberSample> ROLES_ENTRIES_SORTABLE = new EntriesSortableInternalColumn(Queues.ROLES_MAP);

	/**
	 * ROUTED ENTRIES
	 */
	public static final TextColumn<LightMemberSample> ROUTED_ENTRIES_SORTABLE = new EntriesSortableInternalColumn(Queues.ROUTED_QUEUE);

	/**
	 * STATISTICS ENTRIES
	 */
	public static final TextColumn<LightMemberSample> STATISTICS_ENTRIES_SORTABLE = new EntriesSortableInternalColumn(Queues.STATS_MAP);

	/**
	 * USER PREF ENTRIES
	 */
	public static final TextColumn<LightMemberSample> USER_PREF_ENTRIES_SORTABLE = new EntriesSortableInternalColumn(Queues.USER_PREFERENCES_MAP);

	/*
	 * MEMORY COST
	 */
	
	/**
	 * INPUT MEMORY COST
	 */
	public static final TextColumn<LightMemberSample> INPUT_MEMORY_COST_SORTABLE = new MemoryCostInternalSortableColumn(Queues.INPUT_QUEUE);

	/**
	 * RUNNING MEMORY COST
	 */
	public static final TextColumn<LightMemberSample> RUNNING_MEMORY_COST_SORTABLE = new MemoryCostInternalSortableColumn(Queues.RUNNING_QUEUE);
	
	/**
	 * OUTPUT MEMORY COST
	 */
	public static final TextColumn<LightMemberSample> OUTPUT_MEMORY_COST_SORTABLE = new MemoryCostInternalSortableColumn(Queues.OUTPUT_QUEUE);
	
	/**
	 * ROUTING MEMORY COST
	 */
	public static final TextColumn<LightMemberSample> ROUTING_MEMORY_COST_SORTABLE = new MemoryCostInternalSortableColumn(Queues.ROUTING_QUEUE);
	
	/**
	 * RESOURCES MEMORY COST
	 */
	public static final TextColumn<LightMemberSample> RESOURCES_MEMORY_COST_SORTABLE = new MemoryCostSortableInternalColumn(Queues.COMMON_RESOURCES_MAP);

	/**
	 * ROLES MEMORY COST
	 */
	public static final TextColumn<LightMemberSample> ROLES_MEMORY_COST_SORTABLE = new MemoryCostSortableInternalColumn(Queues.ROLES_MAP);

	/**
	 * ROUTED MEMORY COST
	 */
	public static final TextColumn<LightMemberSample> ROUTED_MEMORY_COST_SORTABLE = new MemoryCostSortableInternalColumn(Queues.ROUTED_QUEUE);

	/**
	 * STATISTICS MEMORY COST
	 */
	public static final TextColumn<LightMemberSample> STATISTICS_MEMORY_COST_SORTABLE = new MemoryCostSortableInternalColumn(Queues.STATS_MAP);

	/**
	 * USER PREF MEMORY COST
	 */
	public static final TextColumn<LightMemberSample> USER_PREF_MEMORY_COST_SORTABLE = new MemoryCostSortableInternalColumn(Queues.USER_PREFERENCES_MAP);

	/*
	 * Implementations  
	 */

	private static class EntriesSortableColumnByQueue extends TextColumn<LightMemberSample> {
		
		private String queueName;
		
		protected EntriesSortableColumnByQueue(String queueName) {
			this.queueName = queueName;
			setSortable(true);
		}

		@Override
		public String getValue(LightMemberSample memberSample) {
			return  JemConstants.ENTRIES_FORMAT.format(memberSample.getMapsStats().get(queueName).getOwnedEntryCount());
		}
		
	}

	private static class EntriesSortableInternalColumn extends TextColumn<LightMemberSample> {
		
		private String queueOrMap;
		
		protected EntriesSortableInternalColumn(String queueOrMap) {
			this.queueOrMap = queueOrMap;
			setSortable(true);
		}

		@Override
		public String getValue(LightMemberSample memberSample) {
			return JemConstants.ENTRIES_FORMAT.format(memberSample.getInternalMapsStats().get(queueOrMap).getOwnedEntryCount());
		}
		
	}

	
	private static class MemoryCostInternalSortableColumn extends TextColumn<LightMemberSample> {

		private String queueOrMap;
		
		protected MemoryCostInternalSortableColumn(String queueOrMap) {
			this.queueOrMap = queueOrMap;
			setSortable(true);
		}

		@Override
		public String getValue(LightMemberSample memberSample) {
			String data = null;
			long cost = memberSample.getMapsStats().get(queueOrMap).getOwnedEntryMemoryCost();
			if (cost < JemConstants.MB){
				cost = cost / JemConstants.KB;
				data = JemConstants.KB_FORMAT.format(cost);
			} else {
				cost = cost / JemConstants.MB;
				data = JemConstants.MB_FORMAT.format(cost);
			}
			return data;
		}
		
	}

	private static class MemoryCostSortableInternalColumn extends TextColumn<LightMemberSample> {
		
		private String queueOrMap;
		
		protected MemoryCostSortableInternalColumn(String queueOrMap) {
			this.queueOrMap = queueOrMap;
			setSortable(true);
		}
		
		@Override
		public String getValue(LightMemberSample memberSample) {
			String data = null;
			long cost = memberSample.getInternalMapsStats().get(queueOrMap).getOwnedEntryMemoryCost();
			if (cost < JemConstants.MB){
				cost = cost / JemConstants.KB;
				data = JemConstants.KB_FORMAT.format(cost);
			} else {
				cost = cost / JemConstants.MB;
				data = JemConstants.MB_FORMAT.format(cost);
			}
			return data;
		}
	}

}
